package broker.implementation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import bcm.extend.AbstractComponent;
import broker.interfaces.ManagementI;
import broker.interfaces.PublicationI;
import broker.ports.ManagementInboundPort;
import broker.ports.PublicationInboundPort;
import broker.ports.ReceptionOutboundPort;
import connectors.ReceptionsConnector;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import message.MessageFilterI;
import message.MessageI;
import subscriber.interfaces.ReceptionI;

import static bcm.extend.Utils.addOnMap;
import static bcm.extend.Utils.getOnSet;;

@RequiredInterfaces(required = {ReceptionI.class})
@OfferedInterfaces(offered = {ManagementI.class, PublicationI.class})
public class ConcurentBroker extends AbstractComponent {
	
	private Map<String, Set<Subscriber>> subscriptions;
	private Set<String> topics;
	private Map<String, ReceptionOutboundPort> receptionPorts = new HashMap<>();
	
	private ManagementInboundPort managementInboundPort;
	private PublicationInboundPort publicationInboundPort;
	
	public static final String	PUBLI_ACCESS_HANDLER_URI = "pah" ;
	public static final String	MANAG_ACCESS_HANDLER_URI = "mah" ;

	private final ReentrantReadWriteLock hashMapLock ;
	

	public ConcurentBroker(String reflectionInboundPortURI, int nbReadingThreads) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0) ;
		assert	reflectionInboundPortURI != null ;
		
		this.hashMapLock = new ReentrantReadWriteLock() ;
		
		this.createNewExecutorService(PUBLI_ACCESS_HANDLER_URI,
									nbReadingThreads,
									false);
		this.createNewExecutorService(MANAG_ACCESS_HANDLER_URI,
									nbReadingThreads,
									false);
		
		this.managementInboundPort = new ManagementInboundPort(
				this.getExecutorServiceIndex(MANAG_ACCESS_HANDLER_URI),
				this);
		this.publicationInboundPort = new PublicationInboundPort(
				this.getExecutorServiceIndex(PUBLI_ACCESS_HANDLER_URI), 
				this);
		
		this.addPort(managementInboundPort);
		this.addPort(publicationInboundPort);
		
		this.managementInboundPort.publishPort();
		this.publicationInboundPort.publishPort();
		
		this.subscriptions = new HashMap<>();
		this.topics = new HashSet<>();
		
		this.tracer.setTitle("broker component");
		this.tracer.setRelativePosition(0, 0) ;
	}

	
	public void createTopic(String topic) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		topics.add(topic);
		this.logMessage("creation du topic : '"+topic+"'");
		this.hashMapLock.writeLock().unlock() ;
	}
	
	public void createTopics(String[] topics) throws Exception {
		for(String topic : topics) {
			createTopic(topic);
		}
	}
	
	public void destroyTopic(String topic) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		topics.remove(topic);
		this.logMessage("destruction du topic : '"+topic+"'");
		this.hashMapLock.writeLock().unlock() ;
	}
	
	public boolean isTopic(String topic) throws Exception {
		boolean res = false;
		this.hashMapLock.readLock().lock() ;
		try {
			res = topics.contains(topic);
		}finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res;
	}
	
	public String[] getTopics() throws Exception {
		String[] res = null;
		this.hashMapLock.readLock().lock() ;
		try {
			res = topics.toArray(new String[0]);
		}finally {
			this.hashMapLock.readLock().unlock() ;
		}
		return res;
	}
	
	public void subscribe(String topic, String inboundPortUri) throws Exception {
		this.subscribe(topic, null, inboundPortUri);
	}
	
	public void subscribe(String[] topics, String inboundPortUri) throws Exception {
		for(String topic : topics) {
			subscribe(topic, inboundPortUri);
		}
	}
	
	public void subscribe(String topic, MessageFilterI filter, String inboundPortUri) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		if(isTopic(topic)) {
			addOnMap(subscriptions, topic, new Subscriber(inboundPortUri, filter));
			
			if(!receptionPorts.containsKey(inboundPortUri)) {
				ReceptionOutboundPort ROPort = new ReceptionOutboundPort(this);
				this.addPort(ROPort);
				ROPort.publishPort();
				
				this.doPortConnection(
						ROPort.getPortURI(),
						inboundPortUri,
						ReceptionsConnector.class.getCanonicalName()) ;
				
				receptionPorts.put(inboundPortUri, ROPort);
			}
	
			this.logMessage("nouvel abonné : "+inboundPortUri+" sur le topic : '"+topic+"'");
		}
		else {
			String msg = String.format("you can not subscribe to %s because it does not exist", topic);
			throw new Exception(msg);
		}
		this.hashMapLock.writeLock().unlock() ;
	}
	
	public void modifyFilter(String topic, MessageFilterI newFilter, String inboundPortUri) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		if(isTopic(topic) && subscriptions.containsKey(topic)) {
			Subscriber subscriber = getOnSet(subscriptions.get(topic), new Subscriber(inboundPortUri));
			subscriber.setFilter(newFilter);
			this.logMessage("l'abonné "+inboundPortUri+" du topic : '"+topic+"' à ajouté un filtre");
		}
		this.hashMapLock.writeLock().unlock() ;
	}
	
	public void unsubscribe(String topic, String inboundPortUri) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		if(isTopic(topic) && subscriptions.containsKey(topic)) {
			subscriptions.get(topic)
						 .remove(new Subscriber(inboundPortUri));
			this.logMessage("désabonnement de '"+inboundPortUri+"' du topic : '"+topic+"'");
		}
		this.hashMapLock.writeLock().unlock() ;
	}
	
	public void publish(MessageI m, String topic) throws Exception {
		this.logMessage("publish le message '"+m.toString()+" sur le topic '"+topic
				+"'");
        List<Subscriber> subscribers = subscriptions.get(topic)
        		.stream()
        		.filter(s -> s.filterMessage(m))
        		.collect(Collectors.toList());
		//Set<Subscriber> subscribers = subscriptions.get(topic);
        for(Subscriber s : subscribers) {
               ReceptionOutboundPort outPort = this.receptionPorts.get(s.getSubscriber());
               outPort.acceptMessage(m);

        }

  }
	
	
	public void publish(MessageI m, String[] topics) throws Exception {
		
		for(String topic : topics) {
			publish(m, topic);
		}
	}
	
	public void publish(MessageI[] ms, String topic)  throws Exception {
		
		for(MessageI m : ms) {
			publish(m, topic);
		}
	}
	
	public void publish(MessageI[] ms, String[] topics)  throws Exception {
		
		for(MessageI m : ms) {
			for(String topic : topics) {
				publish(m, topic);
			}
		}
	}
		
	@Override
	public void shutdown() throws ComponentShutdownException {
		
		try {
			this.managementInboundPort.unpublishPort();
			this.publicationInboundPort.unpublishPort();
		}
		catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
		
		super.shutdown();
	}
	
	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		try {
			this.managementInboundPort.unpublishPort();
			this.publicationInboundPort.unpublishPort();
		}
		catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
		
		super.shutdownNow();
	}

}

class Subscriber {
	
	private String subscriber;
	private Optional<MessageFilterI> filter;
	
	public Subscriber(String subscriber) {
		this.subscriber = subscriber;
		this.filter = Optional.empty();
	}
	
	public Subscriber(String subscriber, MessageFilterI filter) {
		this.subscriber = subscriber;
		this.filter = Optional.ofNullable(filter);
	}
	
	public void setFilter(MessageFilterI filter) {
		this.filter = Optional.ofNullable(filter);
	}
	
	public String getSubscriber() {
		return subscriber;
	}
	
	public boolean filterMessage(MessageI m) {
		return !this.getFilter().isPresent() || this.getFilter().get().filter(m);
	}
	
	public Optional<MessageFilterI> getFilter() {
		return filter;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof Subscriber)) return false;
		Subscriber sub = (Subscriber) o;
		if(!(sub.subscriber.equals(this.subscriber))) {
			return false;
		}
		return true;
	}
}