package publisher.ports;

import broker.interfaces.PublicationI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import message.MessageI;


public class PublicationOutbountPort 
	   extends AbstractOutboundPort 
	   implements PublicationI 
{
	
	private static final long serialVersionUID = 1L;

	public PublicationOutbountPort(ComponentI owner) throws Exception {
		super(PublicationI.class, owner);
		assert	owner instanceof PublicationI;
	}

	public PublicationOutbountPort(String uri, ComponentI owner) throws Exception {
		super(uri, PublicationI.class, owner);
		assert	owner instanceof PublicationI;
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
		((PublicationI) this.connector).publish(m, topic); 

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
		((PublicationI) this.connector).publish(m, topics);

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
		((PublicationI) this.connector).publish(ms, topic);

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
		((PublicationI) this.connector).publish(ms, topics);

	}

}
