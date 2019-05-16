package subscriber.implementation;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import publisher.ports.ManagementOutboundPort;
import subscriber.interfaces.ReceptionI;
import subscriber.ports.ReceptionInboundPort;
import broker.interfaces.ManagementI;
import connectors.ManagementConnector;

@RequiredInterfaces(required = {ManagementI.class})
@OfferedInterfaces(offered = {ReceptionI.class})
public class ConcurentSubscriber 
	   extends Subscriber
{ 

	private final String chmReflectionIBPUri ;

	public ConcurentSubscriber(String chmReflectionIBPUri) throws Exception {
		
		super(1, 5) ;

		assert	chmReflectionIBPUri != null ;
		
		this.chmReflectionIBPUri = chmReflectionIBPUri ;
		
		this.componenetName = String.format("subscriber %d", ++i);
		
		this.managementOutboundPort = new ManagementOutboundPort(this);
		this.recepetionInboundPort = new ReceptionInboundPort(this);
		
		this.addPort(managementOutboundPort);
		this.addPort(recepetionInboundPort);
		
		this.managementOutboundPort.publishPort();
		this.recepetionInboundPort.publishPort();
		
		this.tracer.setTitle("Concurent Subscriber");
		this.tracer.setRelativePosition(1, 1) ;
	}

	@Override
	public void execute() throws Exception {
				
		super.execute() ;

		logMessage("execute begin");
		
		ReflectionOutboundPort rop = new ReflectionOutboundPort(this) ;
		this.addPort(rop) ;
		rop.publishPort() ;
		
		this.doPortConnection(rop.getPortURI(),
							  chmReflectionIBPUri,
							  ReflectionConnector.class.getCanonicalName());

		String[] manageIBPURI =
				rop.findInboundPortURIsFromInterface(ManagementI.class) ;
		
		logMessage("execute 6");
		
		assert	manageIBPURI != null && manageIBPURI.length == 1 ;
		
		this.doPortConnection(
				this.managementOutboundPort.getPortURI(),
				manageIBPURI[0],
				ManagementConnector.class.getCanonicalName()) ;
		

		this.doPortDisconnection(rop.getPortURI()) ;
		rop.unpublishPort() ;
		rop.destroyPort() ;
		
		Thread.sleep(2000L);
		String[] lesTopics = {"topic1", "topic2", "topic3", "topic4"};
		subscribe(lesTopics,this.recepetionInboundPort.getPortURI());
		
		logMessage("execute end");
	}
}