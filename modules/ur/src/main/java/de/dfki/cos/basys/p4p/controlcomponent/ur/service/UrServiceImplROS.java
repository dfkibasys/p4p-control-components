package de.dfki.cos.basys.p4p.controlcomponent.ur.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.URState.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.URState.WorkState;
import edu.wpi.rail.jrosbridge.ActionClient;
import edu.wpi.rail.jrosbridge.Goal;
import edu.wpi.rail.jrosbridge.Goal.GoalStatusEnum;
import edu.wpi.rail.jrosbridge.JRosbridge.WebSocketType;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.callback.ActionCallback;
import edu.wpi.rail.jrosbridge.messages.actionlib.GoalStatus;

public class UrServiceImplROS implements UrService, ServiceProvider<UrService>, ActionCallback{

	protected final Logger LOGGER;
	private Properties config = null;

	private Ros ros = null;	
	private String protocol = "ws";
	private String host = "localhost";
	private int port = 9090;
	
	private ActionClient movSymActionClient;
	private ActionClient ppSymActionClient;

	private GoalStatusEnum status;
	
	public UrServiceImplROS(Properties config) {
		this.config = config;
		LOGGER =  LoggerFactory.getLogger("URServiceImplROS");
	}
	@Override
	public void moveToSymbolicPosition(String positionName) {
		Goal goal = movSymActionClient.createGoal(this);
		status = GoalStatusEnum.PENDING;
			
		JsonObject symbolicPosition = Json.createObjectBuilder().add("target_pos", positionName).build();
		goal.submit(symbolicPosition);
	}

	@Override
	public void pickAndPlaceSymbolic(String objectType, String sourceLocation, String targetLocation) {
		Goal goal = ppSymActionClient.createGoal(this);
		status = GoalStatusEnum.PENDING;

		JsonObject ppJson = Json.createObjectBuilder().add("source_location", sourceLocation)
													  .add("object_type", objectType)
													  .add("target_location", targetLocation)
				.build();
		goal.submit(ppJson);
	}

	@Override
	public String getStatus() {
		return status.toString();
	}	

	@Override
	public void reset() {
		// Cancel active goals?
		// TODO
	}
	
	
	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		String patternString = "(?<protocol>wss?):\\/\\/(?<host>.*):(?<port>\\d*)";

		Pattern pattern = Pattern.compile(patternString);

		Matcher matcher = pattern.matcher(connectionString);
		boolean matches = matcher.matches();
		if (!matches) {
			LOGGER.error("Invalid external connection string! " + connectionString + " does not match the expected pattern " + patternString);
			return false;
		}
		
		protocol = matcher.group("protocol");
		host = matcher.group("host");
		port = Integer.parseInt(matcher.group("port"));
				
		//TODO: support also wss
		ros = new Ros(host, port, WebSocketType.valueOf(protocol));
		
		if (ros.connect()) {
			// MoveSym
			String movSymServerName = config.getProperty("movSymServerName");
			String movSymActionName = config.getProperty("movSymActionName");
			movSymActionClient = new ActionClient(ros, movSymServerName, movSymActionName);
			movSymActionClient.initialize();
			// Pick&Place
			String ppSymServerName = config.getProperty("ppSymServerName");
			String ppSymActionName = config.getProperty("ppSymActionName");
			ppSymActionClient = new ActionClient(ros, ppSymServerName, ppSymActionName);
			ppSymActionClient.initialize();
			// Pick
			// TODO
			// Join
			// TODO
		}
		else
			LOGGER.error("Connection to specified connection string {} failed!", connectionString);
		
		return ros.isConnected();
	}
	
	
	@Override
	public void disconnect() {
		if (ros.isConnected()) {
			movSymActionClient.dispose();
			ppSymActionClient.dispose();
			ros.disconnect();	
		}
	}
	
	@Override
	public boolean isConnected() {
		return ros == null ? false : ros.isConnected();
	}

	@Override
	public UrService getService() {
		return this;
	}
	
	@Override
	public void handleStatus(GoalStatus goalStatus) {
		LOGGER.debug("STATUS: " + goalStatus.toString());	
		status = GoalStatusEnum.get(goalStatus.getStatus());
	}
	
	@Override
	public void handleResult(JsonObject result) {
		LOGGER.debug("RESULT: " + result.toString());
	}
	
	@Override
	public void handleFeedback(JsonObject feedback) {
		LOGGER.debug("FEEDBK: " + feedback.toString());
	}

	@Override
	public MissionState getMissionState() {
		MissionState mState = null;
		switch(status) {
		case ACTIVE:
			mState = MissionState.EXECUTING;
			break;
		case PENDING:
			mState = MissionState.PENDING;
			break;
		case PREEMPTED:
		case PREEMPTING:
		case RECALLED:
			case RECALLING:
			mState = MissionState.CANCELLED;
			break;
		case LOST:
		case ABORTED:
			mState = MissionState.FAILED;
			break;
		case REJECTED:
			mState = MissionState.REJECTED;
			break;
			case SUCCEEDED:
			mState = MissionState.DONE;
			break;
			default:
				throw new IllegalStateException("Unexpected value: " + status);
		}
		return mState;
	}
	@Override
	public WorkState getWorkState() {
		WorkState wstate = null;
		switch(status) {
			case ABORTED:
			case REJECTED:
			case LOST:
			case RECALLED:
			case PREEMPTED:
			case SUCCEEDED:
				wstate = WorkState.FINISHED;
				break;
			case ACTIVE:
			case RECALLING:
			case PENDING:
			case PREEMPTING:
				wstate = WorkState.BUSY;
		}
		return wstate;
	}

}
