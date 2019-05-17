package test.ut;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import message.Message;
import message.MessageFilterI;
import message.MessageI;
import message.Properties;
import message.TimeStamp;

public abstract class AbstractMessageTest {

	protected MessageI messageProvider;
	protected MessageFilterI messageFilterProvider;
	
	@Before
	public abstract void setUp()throws Exception;
	
	@Test
	public void getUriTest() {
		String uri = "test";
		this.messageProvider = new Message(uri);
		assertEquals(uri,this.messageProvider.getURI());
	}
	
	@Test
	public void getTimeStampTest() {
		TimeStamp timestamp = new TimeStamp(10,"10janvier2018");
		this.messageProvider = new Message(timestamp);
		assertEquals(timestamp,this.messageProvider.getTimeStamp());
	}
	
	@Test
	public void getPropertiesTest() {
		Properties properties = new Properties();
		properties.putProp("nombre", 10);
		this.messageProvider = new Message(properties);
		assertEquals(properties.getIntProp("nombre"),this.messageProvider.getProperties().getIntProp("nombre"));
	}
	
	@Test
	public void getPlayloadTest() {
		Serializable content = "Hello World";
		this.messageProvider = new Message(content);
		assertEquals(content.toString(), this.messageProvider.getPayload().toString());
	}
	
}
