package test.it;

import message.Message;
import publisher.implementation.Publisher;
import subscriber.implementation.Subscriber;

public class ItTest1 {
	
	
	
	public static void main(String[] args) throws Exception {
		
		ItManager itManager = new ItManager(1, 1);
		
		
		itManager.setScenario(
				
				() -> {
					Publisher[] p = itManager.getPublishers();
					Subscriber[] s = itManager.getSubscribers();
					
					String[] lesTopics = {"T1", "T2", "T3","T4"};
					
					p[0].setComponentBehavior(
							() -> {
								p[0].createTopics(lesTopics);
								Thread.sleep(10000L);
								p[0].publish(new Message("hello World"), lesTopics);
								p[0].publish(new Message("Tomate"), "topic2");
								p[0].publish(new Message("Patata"), "topic3");
								Thread.sleep(10000L);
								p[0].destroyTopic("topic1");
								p[0].destroyTopic("topic4");
							}
					);
					
					s[0].setComponentBehavior(
							() -> {
								Thread.sleep(2000L);
								s[0].subscribe(lesTopics, s[0].getRecepetionInboundPort().getPortURI());
							}
					);
				}
		);
		
		itManager.run();
		
		
	}

}
