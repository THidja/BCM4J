package subscriber.implementation;

import bcm.extend.AbstractComponent;
import bcm.extend.Environment;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.examples.chm.connectors.MapReadingConnector;
import fr.sorbonne_u.components.examples.chm.connectors.MapWritingConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.reflection.connectors.ReflectionConnector;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;
import message.Message;
import message.MessageFilterI;
import message.MessageI;
import publisher.ports.ManagementOutboundPort;
import subscriber.interfaces.ReceptionI;
import subscriber.interfaces.ReceptionImplementationI;
import subscriber.ports.ReceptionInboundPort;
import broker.interfaces.ManagementI;
import broker.interfaces.ManagementImplementationI;
import broker.interfaces.PublicationI;
import broker.interfaces.SubscriptionImplementationI;
import connectors.ManagementConnector;
import connectors.ReceptionsConnector;

@RequiredInterfaces(required = {ManagementI.class})
@OfferedInterfaces(offered = {ReceptionI.class})
public class ConcurentSubscriber 
	   extends AbstractComponent
	   implements ReceptionImplementationI, SubscriptionImplementationI
{ 

	private static int i  = 0;
	private String componenetName;
	
	private ManagementOutboundPort managementOutboundPort;
	private ReceptionInboundPort recepetionInboundPort;
	
	protected final String chmReflectionIBPUri ;

					
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
		
		this.tracer.setTitle("subscriber component");
		this.tracer.setRelativePosition(1, 1) ;
	}
	
	@Override
	public void acceptMessage(MessageI m) throws Exception {
		this.logMessage(String.format("%s received %s", this.componenetName, m.toString()));
	}
	
	@Override
	public void acceptMessages(MessageI[] ms) throws Exception {
		for(MessageI m : ms) {
			this.acceptMessage(m);
		}
	}
	
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

	@Override
	public void subscribe(String topic, String inboundPortUri) throws Exception {
		this.managementOutboundPort.subscribe(topic,inboundPortUri);
		this.logMessage("Abonnement de '"+inboundPortUri+"' au topic '"+topic+"' ");
	}

	@Override
	public void subscribe(String[] topics, String inboundPortUri) throws Exception {
		this.managementOutboundPort.subscribe(topics,inboundPortUri);
		for(String t: topics)
			this.logMessage("Abonnement de '"+inboundPortUri+"' au topic '"+t+"' ");
	}

	@Override
	public void subscribe(String topic, MessageFilterI filter, String inboundPortUri) throws Exception {
		this.managementOutboundPort.subscribe(topic, inboundPortUri);
		this.logMessage("Abonnement de '"+inboundPortUri+"' au topic '"+topic+"' ");
	}

	@Override
	public void modifyFilter(String topic, MessageFilterI newFilter, String inboundPortUri) throws Exception {
		this.managementOutboundPort.modifyFilter(topic, newFilter, inboundPortUri);
		this.logMessage("Modification/ajout de filtre par '"+inboundPortUri+"' au topic '"+topic+"' ");
	}

	@Override
	public void unsubscribe(String topic, String inboundPortUri) throws Exception {
		this.managementOutboundPort.unsubscribe(topic, inboundPortUri);
		this.logMessage("Désabonnement de '"+inboundPortUri+"' du topic '"+topic+"' ");
	}

}