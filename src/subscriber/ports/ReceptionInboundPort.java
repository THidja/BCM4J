package subscriber.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import message.MessageI;
import subscriber.implementation.Subscriber;
import subscriber.interfaces.ReceptionI;

public class ReceptionInboundPort
	   extends AbstractInboundPort
	   implements ReceptionI
{
	private static final long serialVersionUID = 1L;

	public ReceptionInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ReceptionI.class, owner);
		assert owner instanceof Subscriber;
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return l'abonn�
	 */

	public ReceptionInboundPort(ComponentI owner) throws Exception {
		super(ReceptionI.class, owner);
		assert owner instanceof Subscriber;
	}
	
	private Subscriber owner() {
		return (Subscriber) this.owner;
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * Accepte le message
	 * @throws Exception
	 */
	@Override
	public void acceptMessage(MessageI m) throws Exception {
		this.owner()
			.handleRequestAsync(
					() -> this.owner().acceptMessage(m)
			 );
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI[] ms
	 * Accepte les messages
	 * @throws Exception
	 */
	@Override
	public void acceptMessages(MessageI[] ms) throws Exception {
		this.owner()
			.handleRequestAsync(
					() -> this.owner().acceptMessages(ms)
			);
	}

}
