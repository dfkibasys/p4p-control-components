package de.dfki.cos.basys.p4p.controlcomponent.wallet;

import static org.junit.Assert.*;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.rail.jrosbridge.messages.geometry.Point;
import edu.wpi.rail.jrosbridge.messages.geometry.PointStamped;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.PoseStamped;
import edu.wpi.rail.jrosbridge.messages.geometry.Quaternion;
import edu.wpi.rail.jrosbridge.messages.std.Header;
import edu.wpi.rail.jrosbridge.primitives.Time;

public class LiftTest extends BaseTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testLift() {
		PointStamped point = new PointStamped(
			new Header(0, new Time(), "/map"), 
			new Point(0, 0, 0.33)			
		);		
		
		JsonObject targetPoint = Json.createObjectBuilder().add("target_height", point.toJsonObject()).build();
		
		System.out.println(targetPoint.toString());
			
		/*
		{
			"target_height": {
				"header": {
					"seq": 0,
					"stamp": {
						"secs": 0,
						"nsecs": 0
					},
					"frame_id": "/map"
				},
				"point": {
					"x": 0.0,
					"y": 0.0,
					"z": 0.33
				}
			}
		}
		*/
	}

}
