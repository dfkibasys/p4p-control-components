package de.dfki.cos.basys.p4p.controlcomponent.wallet;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.common.component.ComponentException;
import edu.wpi.rail.jrosbridge.ActionClient;
import edu.wpi.rail.jrosbridge.Goal;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Goal.GoalStatusEnum;
import edu.wpi.rail.jrosbridge.JRosbridge.WebSocketType;
import edu.wpi.rail.jrosbridge.callback.ActionCallback;
import edu.wpi.rail.jrosbridge.messages.actionlib.GoalStatus;
import edu.wpi.rail.jrosbridge.messages.geometry.Point;
import edu.wpi.rail.jrosbridge.messages.geometry.PointStamped;
import edu.wpi.rail.jrosbridge.messages.std.Header;
import edu.wpi.rail.jrosbridge.primitives.Time;

public class LiftTestREAL extends BaseTest {
	
	Logger LOGGER;
	Ros ros;
	ActionClient liftClient = null;
	GoalStatusEnum liftStatus = GoalStatusEnum.PENDING;
	Double height = 0.0;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		LOGGER = LoggerFactory.getLogger(LiftTestREAL.class.toString());
		String connectionString = "ws://192.168.1.100:9090";
		if (connect(connectionString))
			LOGGER.debug("Ros has been connected.");
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		if (disconnect())
			LOGGER.debug("Ros has been disconnected.");
	}

	@Test
	public void testLift() {
		liftStatus = GoalStatusEnum.PENDING;
		
		PointStamped point = new PointStamped(
				new Header(0, new Time(), "/map"), 
				new Point(0, 0, height)			
			);		
			
		JsonObject targetPoint = Json.createObjectBuilder().add("target_height", point.toJsonObject()).build();
		
		Goal goal = liftClient.createGoal(new ActionCallback() {
			
			@Override
			public void handleStatus(GoalStatus goalStatus) {
				LOGGER.debug("LIFT-STATUS: " + goalStatus.toString());	
				liftStatus = GoalStatusEnum.get(goalStatus.getStatus());
			}
			
			@Override
			public void handleResult(JsonObject result) {
				LOGGER.debug("LIFT-RESULT: " + result.toString());
			}
			
			@Override
			public void handleFeedback(JsonObject feedback) {
				LOGGER.debug("LIFT-FEEDBK: " + feedback.toString());
			}
		});
		
		goal.submit(targetPoint);
//		synchronized(this) {
//			try {
//				this.wait();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	
	private boolean connect(String connectionString) throws ComponentException {
		LOGGER.debug("ConnectionString={}.", connectionString);
		String patternString = "(?<protocol>wss?):\\/\\/(?<host>.*):(?<port>\\d*)";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(connectionString);
		boolean matches = matcher.matches();
		if (!matches) {
			throw new ComponentException("Invalid external connection string! " + connectionString + " does not match the expected pattern " + patternString);
		}
		String protocol = matcher.group("protocol");
		String host = matcher.group("host");
		int port = Integer.parseInt(matcher.group("port"));
		ros = new Ros(host, port, WebSocketType.valueOf(protocol));
		if (ros.connect()) {
			String liftServerName = "/wallet_lift";
			String liftActionName = "/lift_server/LiftWalletAction";
			liftClient = new ActionClient(ros, liftServerName, liftActionName);
			liftClient.initialize();
		} else {
			throw new IllegalStateException("Couldn't establish Ros connection.");
		}
		return ros.isConnected();
	}
	
	private boolean disconnect() {
		if (ros.isConnected()) {
			liftClient.dispose();
			ros.disconnect();	
		}
		return !ros.isConnected();
	}

}
