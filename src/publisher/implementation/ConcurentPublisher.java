package publisher.implementation;

import broker.interfaces.ManagementI;
import broker.interfaces.PublicationI;
import connectors.ManagementConnector;
import connectors.PublicationsConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import message.Message;
import publisher.ports.PublicationOutbountPort;
import publisher.ports.ManagementOutboundPort;


public class ConcurentPublisher extends Publisher 
{
	
	private final String chmReflectionIBPUri ;
	
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
		this.tracer.setTitle("Concurent publisher");
		this.tracer.setRelativePosition(1, 0);
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
		
}
