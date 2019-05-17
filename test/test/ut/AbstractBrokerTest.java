package test.ut;


import org.junit.BeforeClass;
import org.junit.Test;

import broker.interfaces.ManagementI;
import broker.interfaces.PublicationI;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;



public abstract class AbstractBrokerTest {
	
	
	protected ManagementI managementProvider;
	protected PublicationI publicationProvider;
	
	@BeforeClass
	public abstract void setUp() throws Exception;
	
	@Test
	public void createTopicTest() throws Exception {
		this.managementProvider.createTopic("Hacking");
		String[] topics = {"Hacking"};
		assertArrayEquals(topics, this.managementProvider.getTopics());
	}
	
	@Test 
	public void getTopicsTest() throws Exception {
		this.managementProvider.createTopic("Hacking");
		String[] topics = {"Hacking"};
		assertArrayEquals(topics, this.managementProvider.getTopics());
	}
	
	@Test
	public void destroyTopicTest() throws Exception {
		this.managementProvider.createTopic("Hacking");
		String[] topics = this.managementProvider.getTopics();
		this.managementProvider.destroyTopic("Hacking");
		assertFalse(Arrays.asList(topics).contains("Hacking"));		
	}
	
	@Test
	public void isTopicTest() throws Exception {
		this.managementProvider.createTopic("Hacking");
		assertTrue(this.managementProvider.isTopic("Hacking"));
	}
	
}
