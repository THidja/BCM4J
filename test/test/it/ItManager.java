package test.it;

import broker.implementation.Broker;
import connectors.ManagementConnector;
import connectors.ReceptionsConnector;
import connectors.PublicationsConnector;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import publisher.implementation.Publisher;
import subscriber.implementation.Subscriber;

public class ItManager {
	
	private Publisher[] publishers;
	private Subscriber[] subscribers;
	private Broker broker;
	private Scenario scenario;
	
	public ItManager(int nbPublisher, int nbSubscriber) {
		this.publishers = new Publisher[nbPublisher];
		this.subscribers = new Subscriber[nbSubscriber];
	}
	
	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
	
	public void run() throws Exception {
		
		AbstractCVM cvm = new AbstractCVM() {
			
			@Override
			public void deploy() throws Exception {
				
				String brokerManIn = "BROKER_MANAGEMENT_IN_PORT_URI";
				String brokerPubIn = "BROKER_PUBLICATION_IN_PORT_URI";
				broker = new Broker(brokerPubIn, brokerManIn);
				
				for(int i = 0; i < publishers.length; i++) {
					
					String publisherPubOut = "PUBLISHER_PUBLICATION_OUT_PORT_URI_" + (i+1);
					String publisherManOut = "PUBLISHER_MANAGEMENT_OUT_PORT_URI_" + (i+1);
					
					publishers[i] = new Publisher(publisherPubOut, publisherManOut);
					publishers[i].toggleTracing();
					publishers[i].doPortConnection(publisherManOut,
												   brokerManIn,
												   ManagementConnector.class.getCanonicalName());
					publishers[i].doPortConnection(publisherPubOut,
												   brokerPubIn,
												   PublicationsConnector.class.getCanonicalName());
				}
				for(int i = 0; i < subscribers.length; i++) {
					
					String subscriberRecIn = "SUBSCRIBER_RECEPTION_IN_PORT_URI_" + (i+1);
					String subscriberManOut = "SUBSCRIBER_MANAGEMENT_OUT_PORT_URI_" + (i+1);
					String brokerRecOut = "BROKER_RECEPTION_OUT_PORT_URI_" + (i+1);
					
					subscribers[i] = new Subscriber(subscriberManOut, subscriberRecIn);
					subscribers[i].toggleTracing();
					broker.addReceptionOutboundPort(brokerRecOut, subscriberRecIn);
					subscribers[i].doPortConnection(subscriberManOut,
													brokerManIn,
													ManagementConnector.class.getCanonicalName());
					broker.doPortConnection(brokerRecOut,
											subscriberRecIn,
											ReceptionsConnector.class.getCanonicalName());
				}
				
					broker.toggleTracing();
				
				scenario.distribute();
				
				super.deploy();
			}
		};
		cvm.startStandardLifeCycle(20000L);
		Thread.sleep(10000L);
		System.exit(0);
	}
	
	public Subscriber[] getSubscribers() {
		return subscribers;
	}
	
	public Publisher[] getPublishers() {
		return publishers;
	}
	
	public Broker getBroker() {
		return broker;
	}

}

@FunctionalInterface
interface Scenario {
	
	public void distribute() throws Exception;
}