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
public class Broker extends AbstractComponent {

	private Map<String, Set<Subscriber>> subscriptions;
	private Map<String, ReceptionOutboundPort> receptionPorts;
	private Set<String> topics;
	private Map<String, ReceptionOutboundPort> receptionPorts = new HashMap<>();

	private ManagementInboundPort managementInboundPort;
	private PublicationInboundPort publicationInboundPort;

	protected Broker(int nbThreads, int nbSchedulableThreads, String publicationInboundPortUri,
			String managementInboundPortUri) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0) ;
		assert	reflectionInboundPortURI != null ;

		this.hashMapLock = new ReentrantReadWriteLock() ;

		assert managementInboundPortUri != null;
		assert publicationInboundPortUri != null;

		this.managementInboundPort = new ManagementInboundPort(managementInboundPortUri, this);
		this.publicationInboundPort = new PublicationInboundPort(publicationInboundPortUri, this);

		this.addPort(managementInboundPort);
		this.addPort(publicationInboundPort);

		this.managementInboundPort.publishPort();
		this.publicationInboundPort.publishPort();

		this.subscriptions = new HashMap<>();
		this.receptionPorts = new HashMap<>();
		this.topics = new HashSet<>();

		this.tracer.setTitle("broker component");
		this.tracer.setRelativePosition(0, 0) ;
	}

	public Broker(String publicationInboundPortUri,	String managementInboundPortUri) throws Exception {
		this(1, 0, publicationInboundPortUri, managementInboundPortUri);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String receptionOutboundPortUri , String receptionInboundUri
	 * Associer les ports de subscriber et broker du cote de ReceptionI.
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
	 * Creer le sujet dans la liste des sujets
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
	 * Creer une liste de sujet dans la liste des sujets
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
	 * @return vrai si le sujet est present dans la liste sinon faux
	 * @throws Exception
	 */
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

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return la liste des sujets
	 * @throws Exception
	 */
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

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param String inboundPortUri
	 * Abonne un abonne au sujet avec son port
	 * @throws Exception
	 */
	public void subscribe(String topic, String inboundPortUri) throws Exception {
		this.subscribe(topic, null, inboundPortUri);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String[] topics
	 * @param String inboundPortUri
	 * Abonne un abonne aux sujets avec son port
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
	 * Abonne un abonne au sujet avec un filtre et son port
	 * @throws Exception
	 */
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

			this.logMessage("nouvel abonn� : "+inboundPortUri+" sur le topic : '"+topic+"'");
		}
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param MessageFilterI newFilter
	 * @param String inboundPortUri
	 * Modifier le filtre d'un abonne sur le sujet
	 * @throws Exception
	 */
	public void modifyFilter(String topic, MessageFilterI newFilter, String inboundPortUri) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		if(isTopic(topic) && subscriptions.containsKey(topic)) {
			Subscriber subscriber = getOnSet(subscriptions.get(topic), new Subscriber(inboundPortUri));
			subscriber.setFilter(newFilter);
			this.logMessage("l'abonn� "+inboundPortUri+" du topic : '"+topic+"' � ajout� un filtre");
		}
		this.hashMapLock.writeLock().unlock() ;
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param String inboundPortUri
	 * D�sabonne un abonne sur le sujet
	 * @throws Exception
	 */
	public void unsubscribe(String topic, String inboundPortUri) throws Exception {
		this.hashMapLock.writeLock().lock() ;
		if(isTopic(topic) && subscriptions.containsKey(topic)) {
			subscriptions.get(topic)
						 .remove(new Subscriber(inboundPortUri));
			this.logMessage("d�sabonnement de '"+inboundPortUri+"' du topic : '"+topic+"'");
		}
		this.hashMapLock.writeLock().unlock() ;
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * @param String topic
	 * Publier le message a toutes les abonn�s du sujet
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
	 * Publier le message a toutes les abonnes des sujets
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
	 * Publier les messages a toutes les abonnes du sujet
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
	 * Publier les messages a toutes les abonnes des sujets
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
	 * @return l'abonne
	 */
	public String getSubscriber() {
		return subscriber;
	}

	/**
	 *
	 * @param MessageI m
	 * @return vrai si le filtre n'est pas present ou on applique le filtre sur le message sinon faux
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
	 * @return false si o est null ou du type subscriber sinon on cree un abonne
	 * @return false si l'abonne n'existe pas sinon true
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
