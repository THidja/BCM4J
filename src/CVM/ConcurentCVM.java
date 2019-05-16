package CVM;

import bcm.extend.Environment;
import broker.implementation.ConcurentBroker;
import connectors.ManagementConnector;
import connectors.PublicationsConnector;
import connectors.ReceptionsConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import publisher.implementation.ConcurentPublisher;
import subscriber.implementation.ConcurentSubscriber;

public class ConcurentCVM extends AbstractCVM {

	protected static final String	CONCURRENT_BROKER_URI = "cbu" ;
	
	public ConcurentCVM() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		
		new Environment(true);
		
		ConcurentBroker b = new ConcurentBroker(CONCURRENT_BROKER_URI, 5);
		ConcurentPublisher p = new ConcurentPublisher(CONCURRENT_BROKER_URI) ;
		ConcurentSubscriber s = new ConcurentSubscriber(CONCURRENT_BROKER_URI) ;
		
		p.toggleTracing() ;
		b.toggleTracing() ;
		s.toggleTracing() ;
		
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			ConcurentCVM cvm = new ConcurentCVM() ;
			cvm.startStandardLifeCycle(20000L) ;
			Thread.sleep(500000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
