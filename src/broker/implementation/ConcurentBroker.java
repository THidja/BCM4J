package broker.implementation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import broker.interfaces.ManagementI;
import broker.interfaces.PublicationI;
import broker.ports.ManagementInboundPort;
import broker.ports.PublicationInboundPort;
import broker.ports.ReceptionOutboundPort;
import connectors.ReceptionsConnector;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import message.MessageFilterI;
import message.MessageI;
import subscriber.interfaces.ReceptionI;

import static bcm.extend.Utils.addOnMap;
import static bcm.extend.Utils.getOnSet;;

@RequiredInterfaces(required = {ReceptionI.class})
@OfferedInterfaces(offered = {ManagementI.class, PublicationI.class})
public class ConcurentBroker extends Broker {
	
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
		
		managementInboundPort = new ManagementInboundPort(
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
		this.receptionPorts = new HashMap<>();
		
		this.tracer.setTitle("Concurent Broker");
		this.tracer.setRelativePosition(0, 0) ;
	}

	@Override
	public void createTopic(String topic) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		topics.add(topic);
		this.logMessage("creation du topic : '"+topic+"'");
		this.hashMapLock.writeLock().unlock() ;
	}
	
	@Override
	public void destroyTopic(String topic) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		topics.remove(topic);
		this.logMessage("destruction du topic : '"+topic+"'");
		this.hashMapLock.writeLock().unlock() ;
	}
	
	@Override
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
	
	@Override
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
	
	
	@Override
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
	
	@Override
	public void modifyFilter(String topic, MessageFilterI newFilter, String inboundPortUri) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		if(isTopic(topic) && subscriptions.containsKey(topic)) {
			Subscriber subscriber = getOnSet(subscriptions.get(topic), new Subscriber(inboundPortUri));
			subscriber.setFilter(newFilter);
			this.logMessage("l'abonné "+inboundPortUri+" du topic : '"+topic+"' à ajouté un filtre");
		}
		this.hashMapLock.writeLock().unlock() ;
	}
	
	@Override
	public void unsubscribe(String topic, String inboundPortUri) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		if(isTopic(topic) && subscriptions.containsKey(topic)) {
			subscriptions.get(topic)
						 .remove(new Subscriber(inboundPortUri));
			this.logMessage("désabonnement de '"+inboundPortUri+"' du topic : '"+topic+"'");
		}
		this.hashMapLock.writeLock().unlock() ;
	}
	
	@Override
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
}
