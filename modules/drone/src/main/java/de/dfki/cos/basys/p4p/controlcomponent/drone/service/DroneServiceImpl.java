package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonObject;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.*;


public class DroneServiceImpl implements DroneService, ServiceProvider<DroneService>{
	private static final Logger LOG = LoggerFactory.getLogger(DroneServiceImpl.class);
	private static final String PREFIX = "MqttAsyncClient-paho-v3";
	private static final Integer QOS = 0;
	IMqttAsyncClient mqttClient = null;
	String clientId = null;
	
	public DroneServiceImpl() {
		clientId = PREFIX + UUID.randomUUID().toString();
	}
	
	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		MemoryPersistence persistence = new MemoryPersistence();
		final MqttConnectOptions options = new MqttConnectOptions();

		options.setCleanSession(true);
		try {
			mqttClient = new MqttAsyncClient(connectionString, clientId, persistence);
		} catch (MqttException e) {
			LOG.error("Generation of MqttAsyncClient failed wih {}!", e);
			return false;
		}
		
		try {
			mqttClient.connect(options, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					LOG.debug(clientId + " successfully connected to {}.", connectionString);	
					
					// Subscribe to drone work state
					String workStateTopic = "Mavic2/state/flightPhase";
					try {
						mqttClient.subscribe(workStateTopic, QOS, (topic, message) -> {
							String sMessage = new String(message.getPayload());
							if (sMessage.contains("Phase Idle")) {
								WorkState.getInstance().setState(WState.PHASE_IDLE);
							} else if (sMessage.contains("Phase 0")) {
								WorkState.getInstance().setState(WState.PHASE0);
							} else if (sMessage.contains("Phase 1")) {
								WorkState.getInstance().setState(WState.PHASE1);
							} else if (sMessage.contains("Phase 2")) {
								WorkState.getInstance().setState(WState.PHASE2);
							}

						}).waitForCompletion();
					} catch (MqttException e) {
						LOG.warn(clientId + " could not subscribe to topic {}!", workStateTopic);		
					}
					
					// Subscribe to drone physical state
					String physicalStateTopic = "Mavic2/state/physical";
					try {
						mqttClient.subscribe(physicalStateTopic, QOS, (topic, message) -> {
							String sMessage = new String(message.getPayload());
							if (sMessage.contains("ONGROUND")) {
								PhysicalState.getInstance().setState(PState.ONGROUND);
							}
							else if (sMessage.contains("TAKINGOFF")) {
								PhysicalState.getInstance().setState(PState.TAKINGOFF);
							}
							else if (sMessage.contains("HOVERING")) {
								PhysicalState.getInstance().setState(PState.HOVERING);
							}
							else if (sMessage.contains("MOVING")) {
								PhysicalState.getInstance().setState(PState.MOVING);
							}
							else if (sMessage.contains("LANDING")) {
								PhysicalState.getInstance().setState(PState.LANDING);
							}
						}).waitForCompletion();
					} catch (MqttException e) {
						LOG.warn(clientId + " could not subscribe to topic {}!", physicalStateTopic);		
					}
				}
				
				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					LOG.warn(clientId + " could not establish connection to {}!", connectionString);
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Establishing connection to {} failed with {}!", connectionString, e);
			return false;
		}

		return true;
	}

	@Override
	public void disconnect() {
		try {
			mqttClient.disconnect().waitForCompletion();
		} catch (MqttException e) {
			LOG.warn(clientId + " failed to disconnect with {}!", e);
		}		
	}

	@Override
	public void moveToSymbolicPosition(String position){
		String requestTopic = "Mavic2/command/moveToKnownPosition/req";
		String responseTopic = "Mavic2/command/moveToKnownPosition/res";

		WorkState.getInstance().addStateListener((oldState, newState) -> {
			if (newState.equals(WState.PHASE_IDLE)) {
				// TODO: Improve for Drone-Home since landing follows Phase Idle here
				MissionState.getInstance().setState(MState.DONE);
			} else if (
					newState.equals(WState.PHASE0) ||
					newState.equals(WState.PHASE1) ||
					newState.equals(WState.PHASE2)
					) {
				MissionState.getInstance().setState(MState.EXECUTING);
			}
		});

		subscribeToResponse(responseTopic);
		
		if ("_HOME_".equals(position)) {
			position = "Drone-Home";
		}
		
		JsonObject pos = Json.createObjectBuilder().add("position", position).build();

		publish(requestTopic, pos.toString());

	}
	
	@Override
	public void moveToPoint(DronePoint point){
		String requestTopic = "Mavic2/command/moveToPoint/req";
		String responseTopic = "Mavic2/command/moveToPoint/res";
	
		WorkState.getInstance().addStateListener((oldState, newState) -> {
			if (newState.equals(WState.PHASE_IDLE)) {
				MissionState.getInstance().setState(MState.DONE);
			} else if (
					newState.equals(WState.PHASE0) ||
					newState.equals(WState.PHASE1) ||
					newState.equals(WState.PHASE2)
					) {
				MissionState.getInstance().setState(MState.EXECUTING);
			}
		});

		subscribeToResponse(responseTopic);
		
		JsonObject position = Json.createObjectBuilder()
				.add("pos", 
						Json.createObjectBuilder()
						.add("x", point.getPos().getX())
						.add("y", point.getPos().getY())
						.add("z", point.getPos().getZ())
						.build()
						)
				.add("rot", point.getRot())
				.add("pitch", point.getPitch())
				.build();
	
		publish(requestTopic, position.toString());
		
	}

	@Override
	public void moveToWaypoints(List<DronePoint> waypoints) {
		String requestTopic = "Mavic2/command/moveToWaypoints/req";
		String responseTopic = "Mavic2/command/moveToWaypoints/res";
	
		WorkState.getInstance().addStateListener((oldState, newState) -> {
			if (newState.equals(WState.PHASE_IDLE)) {
				MissionState.getInstance().setState(MState.DONE);
			} else if (
					newState.equals(WState.PHASE0) ||
					newState.equals(WState.PHASE1) ||
					newState.equals(WState.PHASE2)
					) {
				MissionState.getInstance().setState(MState.EXECUTING);
			}
		});

		subscribeToResponse(responseTopic);
		
		String payload = "";
		try {
			payload = new ObjectMapper().writeValueAsString(waypoints);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		publish(requestTopic, payload);
		
	}

	@Override
	public void takeOff() {
		String responseTopic = "Mavic2/command/takeOffAndHandOverControl/res";
		String requestTopic = "Mavic2/command/takeOffAndHandOverControl/req";

		subscribeToResponse(responseTopic);
		
		PhysicalState.getInstance().addStateListener((oldState, newState) -> {
			if (newState.equals(PState.TAKINGOFF)) {
				MissionState.getInstance().setState(MState.EXECUTING);
			}
			else if (newState.equals(PState.HOVERING)) {
				MissionState.getInstance().setState(MState.DONE);
			}

		});
		
		publish(requestTopic, "");
	}

	@Override
	public void land() {
		String responseTopic = "Mavic2/command/emergencyLanding/res";
		String requestTopic = "Mavic2/command/emergencyLanding/req";

		subscribeToResponse(responseTopic);
		
		PhysicalState.getInstance().addStateListener((oldState, newState) -> {
			if (newState.equals(PState.LANDING)) {
				MissionState.getInstance().setState(MState.EXECUTING);
			}
			else if (newState.equals(PState.ONGROUND)) {
				MissionState.getInstance().setState(MState.DONE);
			}
		});
	
		publish(requestTopic, "");
	}

	@Override
	public void startLiveImage() {
		String responseTopic = "Mavic2/command/startLiveImage/res";
		String requestTopic = "Mavic2/command/startLiveImage/req";

		subscribeToResponse(responseTopic);
		publish(requestTopic, "");
	}

	@Override
	public void stopLiveImage() {
		String responseTopic = "Mavic2/command/stopLiveImage/res";
		String requestTopic = "Mavic2/command/stopLiveImage/req";

		subscribeToResponse(responseTopic);
		publish(requestTopic, "");

	}
	

	@Override
	public void startRTMPStream() {
		String responseTopic = "Mavic2/command/startRTMPStream/res";
		String requestTopic = "Mavic2/command/startRTMPStream/req";

		subscribeToResponse(responseTopic);
		publish(requestTopic, "");
		
	}

	@Override
	public void stopRTMPStream() {
		String responseTopic = "Mavic2/command/stopRTMPStream/res";
		String requestTopic = "Mavic2/command/stopRTMPStream/req";

		subscribeToResponse(responseTopic);
		publish(requestTopic, "");
		
	}
	
	@Override
	public List<String> getSymbolicPositions() {
		// TODO
		return null;
	}

	@Override
	public List<String> detectObstacles(String type) {
		MissionState.getInstance().setState(MState.EXECUTING);

		String serviceEndpoint = "http://localhost:5000/inspection_flight/start-inspection-flight-test";
		List<String> result = Collections.emptyList();
		sleep(5000);
		MissionState.getInstance().setState(MState.DONE);
		// determine the waypoints
		List<DronePoint> waypoints = getDefaultWayPoints();
		Map<String,Object> params = new HashMap<>();
		params.put("waypoints",waypoints);
		com.google.gson.JsonObject json = new com.google.gson.JsonObject();
		json.addProperty("waypoints", String.valueOf(waypoints));
		String payload = new Gson().toJson(params);
		String output = callInspectionFlightEndPoint(serviceEndpoint, payload);
		System.out.println(output);
//		try {
////			String payload = new ObjectMapper().writeValueAsString(params);
//			//post to end point
//			String output = callInspectionFlightEndPoint(serviceEndpoint,payload);
//			System.out.println(output);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		// call the api for detecting obstacles
		// check the output it produces
		if(output.equals(String.valueOf(false))){
			MissionState.getInstance().setState(MState.REJECTED);
		} else if (output.equals(String.valueOf(true))){
			MissionState.getInstance().setState(MState.ACCEPTED);
			//Check for object detection
			MissionState.getInstance().setState(MState.DONE);
		}
		return result;
	}

	private HttpURLConnection createConnection(String URI,boolean isPost){
		try {
			URL url = new URL(URI);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if(isPost){
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content/Type","application/json; utf-8");
				conn.setDoOutput(true);
			} else {
				conn.setRequestMethod("GET");
			}
			conn.setRequestProperty("Accept","application/json");
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String callInspectionFlightEndPoint(String endPoint,String payload){
		HttpURLConnection conn = createConnection(endPoint,true);
		input_post(conn, payload);
		return output_post(conn);
	}

	public void input_post(HttpURLConnection con,String jsonInputString){
		try(OutputStream os=con.getOutputStream()){
			byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
			os.write(input,0,input.length);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public String output_post(HttpURLConnection con){
		try(BufferedReader br1 = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br1.readLine()) != null) {
				response.append(responseLine.trim());
			}

			return (response.toString());
		}
		catch(Exception e){
			return "Error in output post";
		}
	}

	private List<DronePoint> getDefaultWayPoints(){
		List<DronePoint> waypoints = new ArrayList<DronePoint>();
		waypoints.add(new DronePoint(1.6, 0.7, 2.3, 0, -90));
		waypoints.add(new DronePoint(1.6, 2.2, 2.3, 0, -90));
		waypoints.add(new DronePoint(1.6, 3.7, 2.3, 0, -90));
		waypoints.add(new DronePoint(1.6, 5.2, 2.3, 0, -90));
		waypoints.add(new DronePoint(0.5, 5.2, 2.3, 90, -90));
		waypoints.add(new DronePoint(0.5, 3.7, 2.3, -180, -90));
		waypoints.add(new DronePoint(0.5, 2.2, 2.3, -180, -90));
		waypoints.add(new DronePoint(0.5, 0.7, 2.3, -180, -90));
		return waypoints;
	}

	@Override
	public void reset() {
		unsubscribe("Mavic2/command/startLiveImage/res");
		unsubscribe("Mavic2/command/stopLiveImage/res");
		unsubscribe("Mavic2/command/startRTMPStream/res");
		unsubscribe("Mavic2/command/stopRTMPStream/res");
		unsubscribe("Mavic2/command/takeOffAndHandOverControl/res");
		unsubscribe("Mavic2/command/pauseMotion/res");
		unsubscribe("Mavic2/command/continueMotion/res");
		unsubscribe("Mavic2/command/emergencyLanding/res");
		unsubscribe("Mavic2/command/moveToKnownPosition/res");
		unsubscribe("Mavic2/command/moveToPoint/res");
		unsubscribe("Mavic2/command/moveToWaypoints/res");
		
		MissionState.getInstance().setState(MState.PENDING);		
		WorkState.getInstance().removeStateListeners();
		PhysicalState.getInstance().removeStateListeners();
	}

	@Override
	public void pause() {
		String responseTopic = "Mavic2/command/pauseMotion/res";
		String requestTopic = "Mavic2/command/pauseMotion/req";

		subscribeToResponse(responseTopic);
		publish(requestTopic, "");
	}

	@Override
	public void resume() {
		String responseTopic = "Mavic2/command/continueMotion/res";
		String requestTopic = "Mavic2/command/continueMotion/req";

		subscribeToResponse(responseTopic);
		publish(requestTopic, "");
	}

	@Override
	public void abort() {
		String responseTopic = "Mavic2/command/emergencyLanding/res";
		String requestTopic = "Mavic2/command/emergencyLanding/req";
		
		subscribeToResponse(responseTopic);
		publish(requestTopic, "");
	}

	@Override
	public boolean isConnected() {
		return mqttClient != null && mqttClient.isConnected();
	}

	@Override
	public DroneService getService() {
		return this;
	}

	@Override
	public MissionState getMissionState() {
		return MissionState.getInstance();
	}

	@Override
	public WorkState getWorkState() {
		return WorkState.getInstance();
	}

	@Override
	public String getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	private void sleep(long millis) {
		try {
			TimeUnit.MILLISECONDS.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void subscribeToResponse(String responseTopic){
		try {
			mqttClient.subscribe(responseTopic, QOS, (topic, message) -> {
				String sMessage = new String(message.getPayload());
				if (sMessage.contains("Accepted")) {
					MissionState.getInstance().setState(MState.ACCEPTED);
				}
				else // Rejected
				{
					MissionState.getInstance().setState(MState.REJECTED);
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}
	}

	private void publish(String topic, String content) {
		final MqttMessage message = new MqttMessage(content.getBytes());
	    message.setQos(QOS);
	    try {
	    	mqttClient.publish(topic, message).waitForCompletion();
	    	LOG.debug("publishing message {} on topic {}", message, topic);
	    } catch (MqttException e) {
	    	LOG.error("Failed to publish message {} on topic {} with {}", message, topic, e);
	    	e.printStackTrace();
	    }
	}

	private void unsubscribe(String topic) {
		try {
			mqttClient.unsubscribe(topic).waitForCompletion();
		} catch (MqttException e) {
			LOG.warn("Failed to unsubscribe from topic {} with {}!", topic, e);
		}
	}
}
