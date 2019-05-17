package test.it;

import message.Message;
import message.MessageI;
import publisher.implementation.Publisher;
import subscriber.implementation.Subscriber;

public class ItTest3 {
	
	
	
	public static void main(String[] args) throws Exception {
		
		ItManager itManager = new ItManager(2, 6);
		
		MessageI[] plats = {
				new Message("Ce midi on a  mangé au SHANDIKA!"),
				new Message("McDonald's"),
				new Message("La maison de la poutine"),
				new Message("Pataterie"),
				new Message("Dominos Pizza")
		};
		
		MessageI[] restos = {
				new Message("Ce midi j'ai mangé Indien!"),
				new Message("Tartiflette"),
				new Message("Poutine!!!!"),
				new Message("Salade César"),
				new Message("Pizza")
		};
		
		String[] topic = {"Restaurants","Cuisine"};
		
		itManager.setScenario(
				
				() -> {
					Publisher[] p = itManager.getPublishers();
					Subscriber[] s = itManager.getSubscribers();
					
					
					p[0].setComponentBehavior(
							() -> {
								p[0].createTopic("Cuisine");
								p[0].getTopics();
								Thread.sleep(10000L);								
								p[0].publish(plats, "Cuisine");
							}
					);
					p[1].setComponentBehavior(
							() -> {
								p[1].createTopic("Restaurants");
								Thread.sleep(10000L);
								p[1].publish(plats ,topic);
								p[1].publish(restos ,topic);
								Thread.sleep(10000L);
							}
					);
					
					
					s[0].setComponentBehavior(
							() -> {
								Thread.sleep(2000L);
								s[0].subscribe("Cuisine", s[0].getRecepetionInboundPort().getPortURI());
							}
					);
					s[1].setComponentBehavior(
							() -> {
								Thread.sleep(2000L);
								s[1].subscribe("Cuisine", s[1].getRecepetionInboundPort().getPortURI());
							}
					);
					s[2].setComponentBehavior(
							() -> {
								Thread.sleep(2000L);
								s[2].subscribe("Cuisine", s[2].getRecepetionInboundPort().getPortURI());
							}
					);
					s[3].setComponentBehavior(
							() -> {
								Thread.sleep(2000L);
								s[3].subscribe("Restaurants", s[3].getRecepetionInboundPort().getPortURI());
							}
					);
					s[4].setComponentBehavior(
							() -> {
								Thread.sleep(2000L);
								s[4].subscribe("Restaurants", s[4].getRecepetionInboundPort().getPortURI());
							}
					);
					s[5].setComponentBehavior(
							() -> {
								Thread.sleep(2000L);
								s[5].subscribe("Restaurants", s[5].getRecepetionInboundPort().getPortURI());
							}
					);
				}
		);
		
		itManager.run();
		
		
	}

}
