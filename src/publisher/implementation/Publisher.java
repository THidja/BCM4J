package publisher.implementation;

import bcm.extend.AbstractComponent;
import broker.interfaces.ManagementImplementationI;
import broker.interfaces.PublicationsImplementationI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import message.MessageI;
import publisher.ports.PublicationOutbountPort;
import publisher.ports.ManagementOutboundPort;


public class Publisher
extends AbstractComponent
implements PublicationsImplementationI, ManagementImplementationI {

	protected PublicationOutbountPort publicationOutboundPort;
	protected ManagementOutboundPort managementOutboundPort;
	
	protected Publisher(int nbThreads,  int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
	}

	public Publisher(String publicationOutboundPortUri, String managementOutboundPortUri) throws Exception {

		super(1, 0);

		assert publicationOutboundPortUri != null;
		assert managementOutboundPortUri != null;
		// create the port that exposes the required interface
		this.publicationOutboundPort = new PublicationOutbountPort(publicationOutboundPortUri, this);
		this.managementOutboundPort = new ManagementOutboundPort(managementOutboundPortUri, this);
		// add the port to the set of ports of the component
		this.addPort(this.publicationOutboundPort);
		this.addPort(this.managementOutboundPort);

		this.publicationOutboundPort.publishPort();
		this.managementOutboundPort.publishPort();

		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir"));
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home"));
		}
		this.tracer.setTitle("Pusblisher");
		this.tracer.setRelativePosition(1, 0);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * @param String topic
	 * Publier le message a toutes les abonnés du sujet
	 * @throws Exception
	 */
	@Override
	public void publish(MessageI m, String topic) throws Exception {
		this.logMessage(
				String.format("publication du message %s sur le topic %s",
								m.toString(),
								topic
							 )
		);
		this.publicationOutboundPort.publish(m,topic);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * @param String[] topics
	 * publier le message a toutes les abonnés des sujets
	 * @throws Exception
	 */
	@Override
	public void publish(MessageI m, String[] topics) throws Exception {
		for(String topic : topics) {
			this.publish(m, topic);
		}

	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI[] ms
	 * @param String topic
	 * publier les messages a toutes les abonnés du sujet
	 * @throws Exception
	 */
	@Override
	public void publish(MessageI[] ms, String topic) throws Exception {
		for(MessageI m : ms) {
			this.publish(m, topic);
		}

	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI[] ms
	 * @param String[] topics
	 * publier les messages a toutes les abonnés des sujets
	 * @throws Exception
	 */
	@Override
	public void publish(MessageI[] ms, String[] topics) throws Exception {
		for(MessageI m : ms) {
			for(String topic : topics) {
				this.publish(m, topic);
			}
		}
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * Créer le sujet dans la liste des sujets
	 * @throws Exception
	 */
	@Override
	public void createTopic(String topic) throws Exception {
		this.managementOutboundPort.createTopic(topic);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String[] topics
	 * Créer une liste de sujet dans la liste des sujets
	 * @throws Exception
	 */
	@Override
	public void createTopics(String[] topics) throws Exception {
		this.managementOutboundPort.createTopics(topics);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * Supprimer un sujet dans la liste des sujets
	 * @throws Exception
	 */
	@Override
	public void destroyTopic(String topic) throws Exception {
		this.managementOutboundPort.destroyTopic(topic);
	}

	/**
	 * @author Felix,Tahar, Christian,Jonathan
	 * @param String topic
	 * @return vrai si le sujet est présent dans la liste sinon faux
	 * @throws Exception
	 */
	@Override
	public boolean isTopic(String topic) throws Exception {
		return this.managementOutboundPort.isTopic(topic);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return la liste des sujets
	 * @throws Exception
	 */
	@Override
	public String[] getTopics() throws Exception {
		return this.managementOutboundPort.getTopics();
  }

	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.publicationOutboundPort.unpublishPort();
			this.managementOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}


}
