package connectors;

import broker.interfaces.PublicationI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import message.MessageI;


public class PublicationsConnector 
	   extends AbstractConnector 
	   implements PublicationI 
{

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * @param String topic
	 * Publier le message a toutes les abonnés du sujet 
	 * @throws Exception
	 */
	@Override
	public void publish(MessageI m, String topic) throws Exception {
		((PublicationI) this.offering).publish(m,topic) ;
	}
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * @param String[] topics
	 * Publier le message a toutes les abonnés des sujets
	 * @throws Exception
	 */
	@Override
	public void publish(MessageI m, String[] topics) throws Exception {
		((PublicationI) this.offering).publish(m,topics) ;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI[] ms
	 * @param String topic
	 * publier les messages a toutes les abonnés du sujet
	 * @throws Exception
	 */
	@Override
	public void publish(MessageI[] m, String topic) throws Exception {
		((PublicationI) this.offering).publish(m,topic) ;
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI[] ms
	 * @param String[] topics
	 * publier les messages a toutes les abonnés des sujets
	 * @throws Exception
	 */
	@Override
	public void publish(MessageI[] m, String[] topic) throws Exception {
		((PublicationI) this.offering).publish(m,topic) ;
	}

}