package broker.ports;

import broker.implementation.Broker;
import broker.interfaces.ManagementI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.examples.chm.components.ConcurrentMapComponent;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import message.MessageFilterI;

public class ManagementInboundPort 
	   extends AbstractInboundPort
	   implements ManagementI
{
	private static final long serialVersionUID = 1L;
	protected final int	executorIndex ;

	public ManagementInboundPort(String uri, int executorIndex, ComponentI owner) throws Exception {
		super(uri, ManagementI.class, owner);
		assert	owner.validExecutorServiceIndex(executorIndex) ;
		this.executorIndex = executorIndex ;
	}
	
	public ManagementInboundPort(int executorIndex, ComponentI owner) throws Exception {
		super(ManagementI.class, owner);
		assert	owner.validExecutorServiceIndex(executorIndex) ;
		this.executorIndex = executorIndex ;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return broker
	 */
	private Broker owner() {
		return (Broker) owner;
	}
	
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * Créer le sujet
	 * @throws Exception
	 */
	@Override
	public void createTopic(String topic) throws Exception {
		/*
		this.owner()
			.handleRequestAsync(
				() ->  this.owner().createTopic(topic)	
			);
		*/
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).createTopic(topic) ;
						return null;
					}
				}) ;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String[] topics
	 * Créer plusieurs sujets
	 * @throws Exception
	 */
	@Override
	public void createTopics(String[] topics) throws Exception {

		/*this.owner()
			.handleRequestAsync(
				() -> this.owner().createTopics(topics)
			);*/
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).createTopics(topics);
						return null;
					}
				}) ;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * Supprime le sujet
	 * @throws Exception
	 */
	@Override
	public void destroyTopic(String topic) throws Exception {
		
		/*this.owner()
			.handleRequestAsync(
				() -> this.owner().destroyTopic(topic)
			);*/
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).destroyTopic(topic);
						return null;
					}
				}) ;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @return Vrai si le sujet existe sinon faux
	 * @throws Exception
	 */
	@Override
	public boolean isTopic(String topic) throws Exception {
		
		/*return this.owner()
				   .handleRequestSync(
						   () -> this.owner().isTopic(topic)
					);*/
		return this.getOwner().handleRequestSync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Boolean>() {
					@SuppressWarnings("unchecked")
					@Override
					public Boolean call() throws Exception {
						return ((Broker) this.getOwner()).isTopic(topic);
					}
				}) ;
		
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return la liste des sujets
	 * @throws Exception
	 */
	@Override
	public String[] getTopics() throws Exception {
		
		/*return this.owner()
				   .handleRequestSync(
						   () -> this.owner().getTopics()
					);	*/
		return this.getOwner().handleRequestSync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<String[]>() {
					@SuppressWarnings("unchecked")
					@Override
					public String[] call() throws Exception {
						return ((Broker) this.getOwner()).getTopics();
					}
				}) ;
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
		
		/*this.owner()
			.handleRequestAsync(
				() -> this.owner().subscribe(topic, inboundPortUri)
			);*/
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).subscribe(topic,inboundPortUri) ;
						return null;
					}
				}) ;
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String[] topics
	 * @param String inboundPortUri
	 * Abonne un abonné aux sujets avec son port
	 * @throws Exception
	 */
	@Override
	public void subscribe(String[] topics, String inboundPortUri) throws Exception {
		
		/*this.owner()
			.handleRequestAsync(
				() -> this.owner().subscribe(topics, inboundPortUri)
			);*/
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).subscribe(topics,inboundPortUri) ;
						return null;
					}
				}) ;
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
		
		/*this.owner()
			.handleRequestAsync(
				() -> this.owner().subscribe(topic, filter, inboundPortUri)
			);*/
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).subscribe(topic,filter,inboundPortUri) ;
						return null;
					}
				}) ;
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
		
		/*this.owner()
			.handleRequestAsync(
				() -> this.owner().modifyFilter(topic, newFilter, inboundPortUri)
			);*/
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).modifyFilter(topic,newFilter,inboundPortUri) ;
						return null;
					}
				}) ;
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
		
		/*this.owner()
			.handleRequestAsync(
				() -> this.owner().unsubscribe(topic, inboundPortUri)
			);*/
		this.getOwner().handleRequestAsync(
				executorIndex,			// identifies the pool of threads to be used
				new AbstractComponent.AbstractService<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void call() throws Exception {
						((Broker) this.getOwner()).unsubscribe(topic,inboundPortUri) ;
						return null;
					}
				}) ;
	}
}