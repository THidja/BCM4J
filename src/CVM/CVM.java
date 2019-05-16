package CVM;

import bcm.extend.Environment;
import broker.implementation.Broker;
import connectors.ManagementConnector;
import connectors.PublicationsConnector;
import connectors.ReceptionsConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import publisher.implementation.Publisher;
import subscriber.implementation.Subscriber;

public class CVM extends AbstractCVM {

	protected static final String	CONCURRENT_BROKER_URI = "cbu" ;
	
	public CVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		
		new Environment(true);
		
		Broker b = new Broker(CONCURRENT_BROKER_URI, 5);
		Publisher p = new Publisher(CONCURRENT_BROKER_URI) ;
		Subscriber s = new Subscriber(CONCURRENT_BROKER_URI) ;
		
		p.toggleTracing() ;
		b.toggleTracing() ;
		s.toggleTracing() ;
		
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM cvm = new CVM() ;
			cvm.startStandardLifeCycle(20000L) ;
			Thread.sleep(500000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
