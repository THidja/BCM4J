package connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import message.MessageI;
import subscriber.interfaces.ReceptionI;

public class ReceptionsConnector
	   extends AbstractConnector
	   implements ReceptionI
{

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * Accepte le message
	 * @throws Exception
	 */
	@Override
	public void acceptMessage(MessageI m) throws Exception {
		((ReceptionI) this.offering).acceptMessage(m);
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String[] m
	 * Accepte les messages
	 * @throws Exception
	 */
	@Override
	public void acceptMessages(MessageI[] m) throws Exception {
		((ReceptionI) this.offering).acceptMessages(m);
	}

}
