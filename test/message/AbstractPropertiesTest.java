package message;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractPropertiesTest {
	
	@Before
	public abstract void setUp()throws Exception;
	
	@Test
	public void PropTest() {
		Properties properties = new Properties();
		properties.putProp("sujet1", true);
		assertEquals(true,properties.getBooleanProp("sujet1"));
	}

}
