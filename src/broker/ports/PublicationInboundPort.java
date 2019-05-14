package broker.ports;

import broker.implementation.Broker;
import broker.interfaces.PublicationI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import message.MessageI;


public class PublicationInboundPort 
	   extends AbstractInboundPort 
	   implements PublicationI 
{

	private static final long serialVersionUID = 1L;
	protected final int	executorIndex ;

	public PublicationInboundPort(int executorIndex, ComponentI owner) throws Exception {
		super(PublicationI.class, owner);
		assert	owner.validExecutorServiceIndex(executorIndex) ;
		this.executorIndex = executorIndex ;
	}

	public PublicationInboundPort(String uri, int executorIndex, ComponentI owner) throws Exception {
		super(uri, PublicationI.class, owner);
		assert	owner.validExecutorServiceIndex(executorIndex) ;
		this.executorIndex = executorIndex ;
	}
	
	private Broker owner() {
		return (Broker) owner;
	}

	@Override
	public void publish(MessageI m, String topic) throws Exception {
		
//		this.owner()
//			.handleRequestAsync(
//				() -> this.owner().publish(m, topic)
//			);
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).publish(m,topic) ;
						return null;
					}
				}) ;
	}

	@Override
	public void publish(MessageI m, String[] topics) throws Exception {
		
//		this.owner()
//			.handleRequestAsync(
//				() -> this.owner().publish(m, topics)
//			);
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).publish(m,topics) ;
						return null;
					}
				}) ;
	}

	@Override
	public void publish(MessageI[] ms, String topic) throws Exception {
		
//		this.owner()
//			.handleRequestAsync(
//				() -> this.owner().publish(ms, topic)
//			);
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).publish(ms,topic) ;
						return null;
					}
				}) ;
	}

	@Override
	public void publish(MessageI[] ms, String[] topics) throws Exception {
		
//		this.owner()
//			.handleRequestAsync(
//				() -> this.owner().publish(ms, topics)
//			);
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).publish(ms,topics) ;
						return null;
					}
				}) ;
	}

}
