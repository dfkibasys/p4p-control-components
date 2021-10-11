package de.dfki.cos.basys.p4p.platform.bdewvs.flink;

import static org.junit.Assert.*;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeserialisationTest {

	String stringData = "{\r\n" + 
			"  \"Signal\": \"[2:5]\",\r\n" + 
			"  \"Value\": 48.8792267,\r\n" + 
			"  \"Timestamp\": \"2021-04-27T12:55:05.2857022Z\"\r\n" + 
			"}";
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
			SignalData signalData = objectMapper.readValue(stringData, SignalData.class);
			assertEquals("[2:5]", signalData.getId());
			assertEquals(48.8792267, signalData.getValue(), 0.001);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
