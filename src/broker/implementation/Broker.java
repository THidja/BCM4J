package broker.implementation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import bcm.extend.AbstractComponent;
import broker.interfaces.ManagementI;
import broker.interfaces.PublicationI;
import broker.ports.ManagementInboundPort;
import broker.ports.PublicationInboundPort;
import broker.ports.ReceptionOutboundPort;
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
public class Broker extends AbstractComponent {
	
	private Map<String, Set<Subscriber>> subscriptions;
	private Map<String, ReceptionOutboundPort> receptionPorts;
	private Set<String> topics;
	private ManagementInboundPort managementInboundPort;
	private PublicationInboundPort publicationInboundPort;
	
	protected Broker(int nbThreads, int nbSchedulableThreads, String publicationInboundPortUri,
			String managementInboundPortUri) throws Exception
	{
		super(nbThreads, nbSchedulableThreads);
		
		assert managementInboundPortUri != null;
		assert publicationInboundPortUri != null;
		
		this.managementInboundPort = new ManagementInboundPort(managementInboundPortUri, this);
		this.publicationInboundPort = new PublicationInboundPort(publicationInboundPortUri, this);
		
		this.addPort(managementInboundPort);
		this.addPort(publicationInboundPort);
		
		managementInboundPort.publishPort();
		publicationInboundPort.publishPort();
		
		this.subscriptions = new HashMap<>();
		this.receptionPorts = new HashMap<>();
		this.topics = new HashSet<>();
		
		this.tracer.setTitle("broker component");
	}
	
	public Broker(String publicationInboundPortUri,	String managementInboundPortUri) throws Exception {
		this(1, 0, publicationInboundPortUri, managementInboundPortUri);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String receptionOutboundPortUri , String receptionInboundUri
	 * Associer les ports de subscriber et broker du coté de ReceptionI.
	 * @throws Exception 
	 */
	public void addReceptionOutboundPort(String receptionOutboundPortUri, 
			String receptionInboundPortUri) throws Exception {
		
		assert receptionOutboundPortUri != null;
		assert receptionInboundPortUri != null;
		
		ReceptionOutboundPort receptionOutboundPort = new ReceptionOutboundPort(receptionOutboundPortUri, this);
		this.addPort(receptionOutboundPort);
		receptionOutboundPort.publishPort();
		this.receptionPorts.put(receptionInboundPortUri, receptionOutboundPort);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * Créer le sujet dans la liste des sujets
	 * @throws Exception
	 */
	public void createTopic(String topic) throws Exception {
		if(!isTopic(topic)) {
			topics.add(topic);
		}
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String[] topics
	 * Créer une liste de sujet dans la liste des sujets
	 * @throws Exception
	 */
	public void createTopics(String[] topics) throws Exception {
		for(String topic : topics) {
			createTopic(topic);
		}
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * Supprimer un sujet dans la liste des sujets
	 * @throws Exception
	 */
	public void destroyTopic(String topic) throws Exception {
		if(isTopic(topic)) {
			topics.remove(topic);
		}
	}
	
	/**
	 * @author Felix,Tahar, Christian,Jonathan
	 * @param String topic
	 * @return vrai si le sujet est présent dans la liste sinon faux
	 * @throws Exception
	 */
	public boolean isTopic(String topic) throws Exception {
		return topics.contains(topic);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return la liste des sujets
	 * @throws Exception
	 */
	public String[] getTopics() throws Exception {
		return topics.toArray(new String[0]);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param String inboundPortUri
	 * Abonne un abonné au sujet avec son port
	 * @throws Exception
	 */
	public void subscribe(String topic, String inboundPortUri) throws Exception {
		this.subscribe(topic, null, inboundPortUri);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String[] topics
	 * @param String inboundPortUri
	 * Abonne un abonné aux sujets avec son port
	 * @throws Exception
	 */
	public void subscribe(String[] topics, String inboundPortUri) throws Exception {
		for(String topic : topics) {
			subscribe(topic, inboundPortUri);
		}
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param MessageFilterI filter
	 * @param String inboundPortUri
	 * Abonne un abonné au sujet avec un filtre et son port
	 * @throws Exception
	 */
	public void subscribe(String topic, MessageFilterI filter, String inboundPortUri) throws Exception {
		if(isTopic(topic)) {
			addOnMap(subscriptions, topic, new Subscriber(inboundPortUri, filter));
		}
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param MessageFilterI newFilter
	 * @param String inboundPortUri
	 * Modifier le filtre d'un abonné sur le sujet
	 * @throws Exception
	 */
	public void modifyFilter(String topic, MessageFilterI newFilter, String inboundPortUri) throws Exception {
		if(isTopic(topic) && subscriptions.containsKey(topic)) {
			Subscriber subscriber = getOnSet(subscriptions.get(topic), new Subscriber(inboundPortUri));
			subscriber.setFilter(newFilter);
		}
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param String inboundPortUri
	 * Désabonne un abonné sur le sujet
	 * @throws Exception
	 */
	public void unsubscribe(String topic, String inboundPortUri) throws Exception {
		if(isTopic(topic) && subscriptions.containsKey(topic)) {
			subscriptions.get(topic)
						 .remove(new Subscriber(inboundPortUri));
		}
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * @param String topic
	 * Publier le message a toutes les abonnés du sujet 
	 * @throws Exception
	 */
	public void publish(MessageI m, String topic) throws Exception {
		List<Subscriber> subscribers = subscriptions.get(topic)
													.stream()
													.filter(s -> s.filterMessage(m))
													.collect(Collectors.toList());
		
		for(Subscriber s : subscribers) {
			ReceptionOutboundPort outPort = this.receptionPorts.get(s.getSubscriber());
			outPort.acceptMessage(m);
		}
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * @param String[] topics
	 * publier le message a toutes les abonnés des sujets
	 * @throws Exception
	 */
	public void publish(MessageI m, String[] topics) throws Exception {
		
		for(String topic : topics) {
			publish(m, topic);
		}
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI[] ms
	 * @param String topic
	 * publier les messages a toutes les abonnés du sujet
	 * @throws Exception
	 */
	public void publish(MessageI[] ms, String topic)  throws Exception {
		
		for(MessageI m : ms) {
			publish(m, topic);
		}
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI[] ms
	 * @param String[] topics
	 * publier les messages a toutes les abonnés des sujets
	 * @throws Exception
	 */
	public void publish(MessageI[] ms, String[] topics)  throws Exception {
		
		for(MessageI m : ms) {
			for(String topic : topics) {
				publish(m, topic);
			}
		}
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * Arret
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		
		try {
			this.managementInboundPort.unpublishPort();
			this.publicationInboundPort.unpublishPort();
			for(ReceptionOutboundPort outPort : this.receptionPorts.values()) {
				outPort.unpublishPort();
			}
		}
		catch(Exception e) {
			throw new ComponentShutdownException(e);
		}
		
		super.shutdown();
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * Arret
	 */
	@Override
	public void shutdownNow() throws ComponentShutdownException
	{
		try {
			this.managementInboundPort.unpublishPort();
			this.publicationInboundPort.unpublishPort();
			for(ReceptionOutboundPort outPort : this.receptionPorts.values()) {
				outPort.unpublishPort();
			}
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
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageFilterI filter
	 * Modifie le filtre du message
	 */
	public void setFilter(MessageFilterI filter) {
		this.filter = Optional.ofNullable(filter);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan 
	 * @return l'abonné
	 */
	public String getSubscriber() {
		return subscriber;
	}
	
	/**
	 * 
	 * @param MessageI m
	 * @return true si le filtre n'est pas présent ou on applique le filtre sur le message 
	 */
	public boolean filterMessage(MessageI m) {
		return !this.getFilter().isPresent() || this.getFilter().get().filter(m);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return le filtre
	 */
	public Optional<MessageFilterI> getFilter() {
		return filter;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param Object o
	 * @return false si o est null ou du type subscriber sinon on cree un abonné
	 * @return false si l'abonné n'existe pas sinon true
	 */
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