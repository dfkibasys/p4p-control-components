package de.dfki.cos.basys.p4p.controlcomponent.ur.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import edu.wpi.rail.jrosbridge.ActionClient;
import edu.wpi.rail.jrosbridge.Goal;
import edu.wpi.rail.jrosbridge.Goal.GoalStatusEnum;
import edu.wpi.rail.jrosbridge.JRosbridge.WebSocketType;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.callback.ActionCallback;
import edu.wpi.rail.jrosbridge.messages.actionlib.GoalID;
import edu.wpi.rail.jrosbridge.messages.actionlib.GoalStatus;

public class URServiceImpl implements URService, ServiceProvider<URService>, ActionCallback{

	private Properties config = null;

	private Ros ros = null;	
	private String protocol = "ws";
	private String host = "localhost";
	private int port = 9090;
	
	private ActionClient actionClient;
	
	private Map<String, GoalStatusEnum> stati = new HashMap<String, GoalStatusEnum>();
	private GoalStatusEnum status;
	
	private final Logger LOGGER;	
	
	public URServiceImpl(Properties config) {
		this.config = config;
		LOGGER =  LoggerFactory.getLogger("URServiceImpl");
	}
	@Override
	public void moveToSymbolicPosition(String positionName) {
		Goal goal = actionClient.createGoal(this);
		//stati.put(goal.getId(), GoalStatusEnum.PENDING);
		status = GoalStatusEnum.PENDING;
			
		JsonObject symbolicPosition = Json.createObjectBuilder().add("target_pos", positionName).build();
		goal.submit(symbolicPosition);
		
	}

	@Override
	public GoalStatusEnum getGoalStatus(GoalID goal) {
		return stati.get(goal.getID());
	}
	
	@Override
	public GoalStatusEnum getStatus() {
		return status;
	}	

	@Override
	public void reset() {
		// Cancel active goals?
		// TODO
		stati.clear();	
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
			String serverName = config.getProperty("serverName");
			String actionNameMove = config.getProperty("actionNameMove");
			actionClient = new ActionClient(ros, serverName, actionNameMove);
			actionClient.initialize();
		}
		else
			LOGGER.error("Connection to specified connection string {} failed!", connectionString);
		
		return ros.isConnected();
	}
	
	
	@Override
	public void disconnect() {
		if (ros.isConnected()) {
			actionClient.dispose();
			ros.disconnect();	
		}
	}
	
	@Override
	public boolean isConnected() {
		return ros == null ? false : ros.isConnected();
	}

	@Override
	public URService getService() {
		return this;
	}
	
	@Override
	public void handleStatus(GoalStatus goalStatus) {
		LOGGER.debug("STATUS: " + goalStatus.toString());	
		//stati.put(goalStatus.getGoalID().getID(), GoalStatusEnum.get(goalStatus.getStatus()));	
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

}
