package de.dfki.cos.basys.p4p.controlcomponent.wallet;

import static org.junit.Assert.*;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.rail.jrosbridge.messages.geometry.Point;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.PoseStamped;
import edu.wpi.rail.jrosbridge.messages.geometry.Quaternion;
import edu.wpi.rail.jrosbridge.messages.std.Header;
import edu.wpi.rail.jrosbridge.primitives.Time;

public class GotoTest extends BaseTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testGoto() {
		PoseStamped pose = new PoseStamped(
			new Header(0, new Time(), "/map"), 
			new Pose(
				new Point(6.438778587249939, 2.118885089546935, 0.1016), 
				new Quaternion(0.0, 0.0, -0.7111406609156684, 0.7030497566975087)
			)
		);		
			
		JsonObject targetPose = Json.createObjectBuilder().add("target_pose", pose.toJsonObject()).build();
		
		System.out.println(targetPose.toString());
		
		/*
		{
			"target_pose": {
				"header": {
					"seq": 0,
					"stamp": {
						"secs": 0,
						"nsecs": 0
					},
					"frame_id": "/map"
				},
				"pose": {
					"position": {
						"x": 6.438778587249939,
						"y": 2.118885089546935,
						"z": 0.1016
					},
					"orientation": {
						"x": 0.0,
						"y": 0.0,
						"z": -0.7111406609156684,
						"w": 0.7030497566975087
					}
				}
			}
		}
		*/
		
	}

}
