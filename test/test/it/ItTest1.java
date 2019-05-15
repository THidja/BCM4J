package test.it;

import message.Message;
import publisher.implementation.Publisher;
import subscriber.implementation.Subscriber;

public class ItTest1 {
	
	
	
	public static void main(String[] args) throws Exception {
		
		ItManager itManager = new ItManager(1, 1);
		
		
		itManager.setScenario(
				
				() -> {
					
					System.out.println("executing scenario 1");
					
					Publisher[] p = itManager.getPublishers();
					Subscriber[] s = itManager.getSubscribers();
					
					p[0].setComponentBehavior(
							() -> {
								System.out.println(p[0]);
								String[] lesTopics = {"topic1", "topic", "topic3"};
								String[] lesTopics2 = {"topic3", "topic4"};
								p[0].createTopics(lesTopics);
								p[0].createTopics(lesTopics2);
								Thread.sleep(1000L);
								p[0].publish(new Message("hello World"), lesTopics);
							}
					);
					
					s[0].setComponentBehavior(
							() -> {
								Thread.sleep(1000L);
								String[] lesTopics = {"topic1", "topic2", "topic3"};
								String[] lesTopics2 = {"topic3", "topic4"};
								s[0].subscribe(lesTopics, s[0].getRecepetionInboundPort().getPortURI());
								s[0].subscribe(lesTopics2,s[0].getRecepetionInboundPort().getPortURI());
							}
					);
				}
		);
		
		itManager.run(true);
		
		
	}

}
