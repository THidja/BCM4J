package broker.ports;

import broker.implementation.Broker;
import broker.interfaces.ManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import message.MessageFilterI;

public class ManagementInboundPort 
	   extends AbstractInboundPort
	   implements ManagementI
{
	private static final long serialVersionUID = 1L;

	public ManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ManagementI.class, owner);
		assert owner instanceof Broker;
	}
	
	public ManagementInboundPort(ComponentI owner) throws Exception {
		super(ManagementI.class, owner);
		assert owner instanceof Broker;
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
		
		this.owner()
			.handleRequestAsync(
				() ->  this.owner().createTopic(topic)	
			);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String[] topics
	 * Créer plusieurs sujets
	 * @throws Exception
	 */
	@Override
	public void createTopics(String[] topics) throws Exception {

		this.owner()
			.handleRequestAsync(
				() -> this.owner().createTopics(topics)
			);		
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * Supprime le sujet
	 * @throws Exception
	 */
	@Override
	public void destroyTopic(String topic) throws Exception {
		
		this.owner()
			.handleRequestAsync(
				() -> this.owner().destroyTopic(topic)
			);
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param String topic
	 * @return Vrai si le sujet existe sinon faux
	 * @throws Exception
	 */
	@Override
	public boolean isTopic(String topic) throws Exception {
		
		return this.owner()
				   .handleRequestSync(
						   () -> this.owner().isTopic(topic)
					);		
	}
	
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @return la liste des sujets
	 * @throws Exception
	 */
	@Override
	public String[] getTopics() throws Exception {
		
		return this.owner()
				   .handleRequestSync(
						   () -> this.owner().getTopics()
					);	
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
		
		this.owner()
			.handleRequestAsync(
				() -> this.owner().subscribe(topic, inboundPortUri)
			);
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
		
		this.owner()
			.handleRequestAsync(
				() -> this.owner().subscribe(topics, inboundPortUri)
			);
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
		
		this.owner()
			.handleRequestAsync(
				() -> this.owner().subscribe(topic, filter, inboundPortUri)
			);
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
		
		this.owner()
			.handleRequestAsync(
				() -> this.owner().modifyFilter(topic, newFilter, inboundPortUri)
			);
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
		
		this.owner()
			.handleRequestAsync(
				() -> this.owner().unsubscribe(topic, inboundPortUri)
			);
	}
}