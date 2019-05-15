package broker.ports;


import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import message.MessageI;
import subscriber.interfaces.ReceptionI;


public class ReceptionOutboundPort 
	   extends AbstractOutboundPort 
       implements ReceptionI 
{

	private static final long serialVersionUID = 1L;

	public ReceptionOutboundPort(ComponentI owner) throws Exception {
		super(ReceptionI.class, owner);
		assert owner instanceof ReceptionI;
	}

	public ReceptionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ReceptionI.class, owner);
		assert	owner instanceof ReceptionI;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * Accepte le message
	 * @throws Exception
	 */
	@Override
	public void acceptMessage(MessageI m) throws Exception {
		((ReceptionI) this.connector).acceptMessage(m);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI[] m
	 * Accepte les messages
	 * @throws Exception
	 */
	@Override
	public void acceptMessages(MessageI[] m) throws Exception {
		((ReceptionI) this.connector).acceptMessages(m);
	}

}