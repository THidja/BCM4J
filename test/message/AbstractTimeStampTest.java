package message;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTimeStampTest {

	@Before
	public abstract void setUp()throws Exception;
	
	@Test
	public void isInitialisedTest() {
		TimeStamp timestamper = new TimeStamp(10,"janvier");
		assertEquals(true,timestamper.isInitialised());
	}
	
	@Test
	public void getTimeTest() {
		TimeStamp timestamper = new TimeStamp(10,"janvier");
		assertEquals(10,timestamper.getTime());
	}
	
	@Test
	public void getTimeStampTest() {
		TimeStamp timestamper = new TimeStamp(10,"janvier");
		assertEquals("janvier",timestamper.getTimestamper());
	}
	
	@Test
	public void setTimeTest() {
		TimeStamp timestamper = new TimeStamp(10,"janvier");
		timestamper.setTime(12);
		assertEquals(12,timestamper.getTime());
	}
	
	@Test
	public void setTimeStampTest() {
		TimeStamp timestamper = new TimeStamp(10,"janvier");
		timestamper.setTimestamper("mars");
		assertEquals("mars",timestamper.getTime());
	}
}
