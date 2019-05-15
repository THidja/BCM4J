package publisher.ports;

import broker.interfaces.ManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import message.MessageFilterI;

public class ManagementOutboundPort extends AbstractOutboundPort implements ManagementI {

	private static final long serialVersionUID = 1L;

	public ManagementOutboundPort(ComponentI owner) throws Exception {
		super(ManagementI.class, owner);
		assert	owner instanceof ManagementI;
	}

	public ManagementOutboundPort(String uri, ComponentI owner) 
		throws Exception {
		super(uri, ManagementI.class, owner);
		assert	owner instanceof ManagementI;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * Créer le sujet dans la liste des sujets
	 * @throws Exception
	 */
	@Override
	public void createTopic(String topic) throws Exception {
		((ManagementI) this.connector).createTopic(topic);
		
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String[] topics
	 * Créer une liste de sujet dans la liste des sujets
	 * @throws Exception
	 */
	@Override
	public void createTopics(String[] topics) throws Exception {
		((ManagementI) this.connector).createTopics(topics);
		
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * Supprimer un sujet dans la liste des sujets
	 * @throws Exception
	 */
	@Override
	public void destroyTopic(String topic) throws Exception {
		((ManagementI) this.connector).destroyTopic(topic);
	}

	/**
	 * @author Felix,Tahar, Christian,Jonathan
	 * @param String topic
	 * @return vrai si le sujet est présent dans la liste sinon faux
	 * @throws Exception
	 */
	@Override
	public boolean isTopic(String topic) throws Exception {
		return ((ManagementI) this.connector).isTopic(topic);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return la liste des sujets
	 * @throws Exception
	 */
	@Override
	public String[] getTopics() throws Exception {
		return ((ManagementI) this.connector).getTopics();
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param String inboundPortUri
	 * Abonne un abonné au sujet avec son port
	 * @throws Exception
	 */
	@Override
	public void subscribe(String topic, String inboundPortUri) throws Exception {
		((ManagementI) this.connector).subscribe(topic, inboundPortUri);
		
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String[] topics
	 * @param String inboundPortUri
	 * Abonne un abonné aux sujets avec son port
	 * @throws Exception
	 */
	@Override
	public void subscribe(String[] topic, String inboundPortUri) throws Exception {
		((ManagementI) this.connector).subscribe(topic, inboundPortUri);
		
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param MessageFilterI filter
	 * @param String inboundPortUri
	 * Abonne un abonné au sujet avec un filtre et son port
	 * @throws Exception
	 */
	@Override
	public void subscribe(String topic, MessageFilterI filter, String inboundPortUri) throws Exception {
		((ManagementI) this.connector).subscribe(topic, filter, inboundPortUri);
		
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param MessageFilterI newFilter
	 * @param String inboundPortUri
	 * Modifier le filtre d'un abonné sur le sujet
	 * @throws Exception
	 */
	@Override
	public void modifyFilter(String topic, MessageFilterI newFilter, String inboundPortUri) throws Exception {
		((ManagementI) this.connector).modifyFilter(topic, newFilter, inboundPortUri);
		
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @param String inboundPortUri
	 * Désabonne un abonné sur le sujet
	 * @throws Exception
	 */
	@Override
	public void unsubscribe(String topic, String inboundPortUri) throws Exception {
		((ManagementI) this.connector).unsubscribe(topic, inboundPortUri);
		
	}

}