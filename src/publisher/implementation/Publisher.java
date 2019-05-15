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
		this.tracer.setRelativePosition(1, 1);
	}

	@Override
	public void publish(MessageI m, String topic) throws Exception {
		this.publicationOutboundPort.publish(m,topic);
	}

	@Override
	public void publish(MessageI m, String[] topics) throws Exception {
		this.publicationOutboundPort.publish(m,topics);

	}

	@Override
	public void publish(MessageI[] m, String topic) throws Exception {
		this.publicationOutboundPort.publish(m,topic);

	}

	@Override
	public void publish(MessageI[] m, String[] topics) throws Exception {
		this.publicationOutboundPort.publish(m,topics);
	}

	@Override
	public void createTopic(String topic) throws Exception {
		this.managementOutboundPort.createTopic(topic);	
	}

	@Override
	public void createTopics(String[] topics) throws Exception {
		this.managementOutboundPort.createTopics(topics);
	}

	@Override
	public void destroyTopic(String topic) throws Exception {
		this.managementOutboundPort.destroyTopic(topic);
	}

	@Override
	public boolean isTopic(String topic) throws Exception {
		return this.managementOutboundPort.isTopic(topic);
	}

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
