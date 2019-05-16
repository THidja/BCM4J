package subscriber.implementation;

import bcm.extend.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import message.MessageFilterI;
import message.MessageI;
import publisher.ports.ManagementOutboundPort;
import subscriber.interfaces.ReceptionI;
import subscriber.interfaces.ReceptionImplementationI;
import subscriber.ports.ReceptionInboundPort;
import broker.interfaces.ManagementI;
import broker.interfaces.SubscriptionImplementationI;

@RequiredInterfaces(required = {ManagementI.class})
@OfferedInterfaces(offered = {ReceptionI.class})
public class Subscriber
	   extends AbstractComponent
	   implements ReceptionImplementationI, SubscriptionImplementationI
{

	private static int i  = 0;
	private String componenetName;

	private ManagementOutboundPort managementOutboundPort;
	private ReceptionInboundPort recepetionInboundPort;

	public Subscriber(String managementOutboundPortUri, String recepetionInboundPortUri) throws Exception {
		this(1, 0, managementOutboundPortUri, recepetionInboundPortUri);
	}

	public Subscriber(int nbThreads, int nbSchedulableThreads, String managementOutboundPortUri,
							String recepetionInboundPortUri) throws Exception {

		super(nbThreads, nbSchedulableThreads);
		assert managementOutboundPortUri != null;
		assert recepetionInboundPortUri != null;

		this.componenetName = String.format("subscriber %d", ++i);

		this.managementOutboundPort = new ManagementOutboundPort(managementOutboundPortUri, this);
		this.recepetionInboundPort = new ReceptionInboundPort(recepetionInboundPortUri, this);

		this.addPort(managementOutboundPort);
		this.addPort(recepetionInboundPort);

		this.managementOutboundPort.publishPort();
		this.recepetionInboundPort.publishPort();

		this.tracer.setTitle("subscriber component");
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI m
	 * Accepte le message
	 * @throws Exception
	 */
	@Override
	public void acceptMessage(MessageI m) throws Exception {
		this.logMessage(String.format("%s received %s", this.componenetName, m.toString()));
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * @param MessageI[] ms
	 * Accepte les messages
	 * @throws Exception
	 */
	@Override
	public void acceptMessages(MessageI[] ms) throws Exception {
		for(MessageI m : ms) {
			this.acceptMessage(m);
		}
	}
	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * Arret
	 * @throws Component!shutdownException
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {

		try {
			this.managementOutboundPort.unpublishPort();
			this.recepetionInboundPort.unpublishPort();
		}
		catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}

	/**
	 * @author Felix, Tahar, Christian, Jonathan
	 * Arret
	 * @throws Component!shutdownException
	 */
	@Override
	public void shutdownNow() throws ComponentShutdownException {

		try {
			this.managementOutboundPort.unpublishPort();
			this.recepetionInboundPort.unpublishPort();
		}
		catch(Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdownNow();
	}

	public ReceptionInboundPort getRecepetionInboundPort() {
		return recepetionInboundPort;
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
		this.managementOutboundPort.subscribe(topic,inboundPortUri);
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
		this.managementOutboundPort.subscribe(topics,inboundPortUri);
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
		this.managementOutboundPort.subscribe(topic, inboundPortUri);
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
		this.managementOutboundPort.modifyFilter(topic, newFilter, inboundPortUri);
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
		this.managementOutboundPort.unsubscribe(topic, inboundPortUri);
	}

}
