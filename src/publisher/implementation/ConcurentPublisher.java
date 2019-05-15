package publisher.implementation;

import broker.interfaces.ManagementI;
import broker.interfaces.ManagementImplementationI;
import broker.interfaces.PublicationI;
import broker.interfaces.PublicationsImplementationI;
import connectors.ManagementConnector;
import connectors.PublicationsConnector;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import message.Message;
import message.MessageI;
import publisher.ports.PublicationOutbountPort;
import publisher.ports.ManagementOutboundPort;


public class ConcurentPublisher 
extends AbstractComponent 
implements PublicationsImplementationI, ManagementImplementationI {
	
	protected PublicationOutbountPort publicationOutboundPort;
	protected ManagementOutboundPort managementOutboundPort;
	protected final String	chmReflectionIBPUri ;
	
	public ConcurentPublisher(String chmReflectionIBPUri) throws Exception {
		
		super(1, 5);
		
		assert	chmReflectionIBPUri != null ;
		this.chmReflectionIBPUri = chmReflectionIBPUri ;
		
		// create the port that exposes the required interface
		this.publicationOutboundPort = new PublicationOutbountPort(this);
		this.managementOutboundPort = new ManagementOutboundPort(this);
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
		this.tracer.setRelativePosition(1, 0);
	}

	@Override
	public void publish(MessageI m, String topic) throws Exception {
		this.publicationOutboundPort.publish(m,topic);
		this.logMessage("publish '"+m.toString()+"' sur le topic '"+topic+"'" );
	}

	@Override
	public void publish(MessageI m, String[] topics) throws Exception {
		this.publicationOutboundPort.publish(m,topics);
		for(String t: topics)
			this.logMessage("publish '"+m.toString()+"' sur le topic '"+t+"'" );

	}

	@Override
	public void publish(MessageI[] m, String topic) throws Exception {
		this.publicationOutboundPort.publish(m,topic);
		for(MessageI mess: m)
			this.logMessage("publish '"+mess.toString()+"' sur le topic '"+topic+"'" );

	}

	@Override
	public void publish(MessageI[] m, String[] topics) throws Exception {
		this.publicationOutboundPort.publish(m,topics);
		for(String t: topics)
			for(MessageI mess: m)
				this.logMessage("publish '"+mess.toString()+"' sur le topic '"+t+"'" );
	}

	@Override
	public void createTopic(String topic) throws Exception {
		this.managementOutboundPort.createTopic(topic);	
		this.logMessage("creation du topic'"+topic+"'");
	}

	@Override
	public void createTopics(String[] topics) throws Exception {
		this.managementOutboundPort.createTopics(topics);
		for(String t: topics)
			this.logMessage("creation du topic'"+t+"'");
	}

	@Override
	public void destroyTopic(String topic) throws Exception {
		this.managementOutboundPort.destroyTopic(topic);
		this.logMessage("destruction du topic '"+topic+"'");
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
	public void execute() throws Exception {
				
		super.execute() ;

		ReflectionOutboundPort rop = new ReflectionOutboundPort(this) ;
		this.addPort(rop) ;
		rop.publishPort() ;

		this.doPortConnection(rop.getPortURI(),
							  chmReflectionIBPUri,
							  ReflectionConnector.class.getCanonicalName());
		String[] publiIBPURI =
				rop.findInboundPortURIsFromInterface(PublicationI.class) ;
		assert	publiIBPURI != null && publiIBPURI.length == 1 ;
		this.doPortConnection(
				this.publicationOutboundPort.getPortURI(),
				publiIBPURI[0],
				PublicationsConnector.class.getCanonicalName()) ;

		String[] manageIBPURI =
				rop.findInboundPortURIsFromInterface(ManagementI.class) ;
		assert	manageIBPURI != null && manageIBPURI.length == 1 ;
		this.doPortConnection(
				this.managementOutboundPort.getPortURI(),
				manageIBPURI[0],
				ManagementConnector.class.getCanonicalName()) ;

		this.doPortDisconnection(rop.getPortURI()) ;
		rop.unpublishPort() ;
		rop.destroyPort() ;

		String[] lesTopics = {"topic1", "topic2", "topic3","topic4"};
		createTopics(lesTopics);
		Thread.sleep(10000L);
		publish(new Message("hello World"), lesTopics);
		publish(new Message("Tomate"), "topic2");
		publish(new Message("Patata"), "topic3");
		Thread.sleep(10000L);
		destroyTopic("topic1");
		destroyTopic("topic4");
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
