package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonObject;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.*;


public class DroneServiceImplMqtt implements DroneService, ServiceProvider<DroneService>{
	private static final Logger LOG = LoggerFactory.getLogger(DroneServiceImplMqtt.class);
	private static final String PREFIX = "MqttAsyncClient-paho-v3";
	private static final Integer QOS = 0;
	IMqttAsyncClient mqttClient = null;
	String clientId = null;
	
	public DroneServiceImplMqtt() {
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
						mqttClient.subscribe(workStateTopic, QOS, new IMqttMessageListener() {
							
							@Override
							public void messageArrived(String topic, MqttMessage message) throws Exception {
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
								
							}
						}).waitForCompletion();
					} catch (MqttException e) {
						LOG.warn(clientId + " could not subscribe to topic {}!", workStateTopic);		
					}
					
					// Subscribe to drone physical state
					String physicalStateTopic = "Mavic2/state/physical";
					try {
						mqttClient.subscribe(physicalStateTopic, QOS, new IMqttMessageListener() {
							
							@Override
							public void messageArrived(String topic, MqttMessage message) throws Exception {
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
			LOG.error("Estalishing connection to {} failed with {}!", connectionString, e);
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
	public boolean isConnected() {
		return mqttClient == null ? false : mqttClient.isConnected();
	}

	@Override
	public DroneService getService() {
		return this;
	}


	@Override
	public void moveToSymbolicPosition(String position){
		String requestTopic = "Mavic2/command/moveToKnownPosition/req";
		String responseTopic = "Mavic2/command/moveToKnownPosition/res";

		WorkState.getInstance().addStateListener(new WorkStateListener() {

			@Override
			public void stateChangedEvent(WState oldState, WState newState) {

				if (newState.equals(WState.PHASE_IDLE)) {
					MissionState.getInstance().setState(MState.DONE);
				} else if (newState.equals(WState.PHASE0)) {
					MissionState.getInstance().setState(MState.EXECUTING);
				} else if (newState.equals(WState.PHASE1)) {
					MissionState.getInstance().setState(MState.EXECUTING);
				} else if (newState.equals(WState.PHASE2)) {
					MissionState.getInstance().setState(MState.EXECUTING);
				}
			}
				
		});
		
		try {
			mqttClient.subscribe(responseTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sResponse = new String(message.getPayload());
					LOG.debug("Got response " + sResponse);
					
					if (sResponse.contains("Accepted")) {
						MissionState.getInstance().setState(MState.ACCEPTED);			
					}
					else // Rejected
					{
						MissionState.getInstance().setState(MState.REJECTED);	
					}
						
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}
		
		if ("_HOME_".equals(position)) {
			position = "Drone-Home";
		}

		publish(requestTopic, "{\"position\": \"" + position + "\"}");

	}
	
	@Override
	public void pause() {
		String responseTopic = "Mavic2/command/pauseMotion/res";
		String requestTopic = "Mavic2/command/pauseMotion/req";
		
		try {
			mqttClient.subscribe(responseTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						MissionState.getInstance().setState(MState.ACCEPTED);			
					}
					else // Rejected
					{
						MissionState.getInstance().setState(MState.REJECTED);	
					}				
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}

		publish(requestTopic, "");
	}
	
	@Override
	public void resume() {
		String responseTopic = "Mavic2/command/continueMotion/res";
		String requestTopic = "Mavic2/command/continueMotion/req";
		
		try {
			mqttClient.subscribe(responseTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						MissionState.getInstance().setState(MState.ACCEPTED);			
					}
					else // Rejected
					{
						MissionState.getInstance().setState(MState.REJECTED);	
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}

		publish(requestTopic, "");
	}

	@Override
	public void abort() {
		String responseTopic = "Mavic2/command/emergencyLanding/res";
		String requestTopic = "Mavic2/command/emergencyLanding/req";
		
		try {
			mqttClient.subscribe(responseTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						MissionState.getInstance().setState(MState.ACCEPTED);			
					}
					else // Rejected
					{
						MissionState.getInstance().setState(MState.REJECTED);	
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}

		publish(requestTopic, "from ControlComponent");
	}
	
	@Override
	public void takeOff() {
		String responseTopic = "Mavic2/command/takeOffAndHandOverControl/res";
		String requestTopic = "Mavic2/command/takeOffAndHandOverControl/req";

		try {
			mqttClient.subscribe(responseTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						MissionState.getInstance().setState(MState.ACCEPTED);			
					}
					else // Rejected
					{
						MissionState.getInstance().setState(MState.REJECTED);	
					}		
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}
		
		PhysicalState.getInstance().addStateListener(new PhysicalStateListener() {

			@Override
			public void stateChangedEvent(PState oldState, PState newState) {
				if (newState.equals(PState.TAKINGOFF)) {
					MissionState.getInstance().setState(MState.EXECUTING);		
				}
				else if (newState.equals(PState.HOVERING)) {
					MissionState.getInstance().setState(MState.DONE);		
				}
				
			}
			
		});		
		
		publish(requestTopic, "");
	}

	@Override
	public void land() {
		// TODO Maybe decouple this from MoveToLocation(Home);
		moveToSymbolicPosition("_HOME_");
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

	@Override
	public void startLiveImage() {
		String responseTopic = "Mavic2/command/startLiveImage/res";
		String requestTopic = "Mavic2/command/startLiveImage/req";
		try {
			mqttClient.subscribe(responseTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						MissionState.getInstance().setState(MState.ACCEPTED);			
					}
					else // Rejected
					{
						MissionState.getInstance().setState(MState.REJECTED);	
					}			
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}
		
		publish(requestTopic, "");
	}

	@Override
	public void stopLiveImage() {
		String responseTopic = "Mavic2/command/stopLiveImage/res";
		String requestTopic = "Mavic2/command/stopLiveImage/req";
		try {
			mqttClient.subscribe(responseTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						MissionState.getInstance().setState(MState.ACCEPTED);			
					}
					else // Rejected
					{
						MissionState.getInstance().setState(MState.REJECTED);	
					}
					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}
		
		publish(requestTopic, "");

	}
	

	@Override
	public void startRTMPStream() {
		String responseTopic = "Mavic2/command/startRTMPStream/res";
		String requestTopic = "Mavic2/command/startRTMPStream/req";
		try {
			mqttClient.subscribe(responseTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						MissionState.getInstance().setState(MState.ACCEPTED);			
					}
					else // Rejected
					{
						MissionState.getInstance().setState(MState.REJECTED);	
					}

				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}
		
		publish(requestTopic, "");
		
	}

	@Override
	public void stopRTMPStream() {
		String responseTopic = "Mavic2/command/stopRTMPStream/res";
		String requestTopic = "Mavic2/command/stopRTMPStream/req";
		try {
			mqttClient.subscribe(responseTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						MissionState.getInstance().setState(MState.ACCEPTED);			
					}
					else // Rejected
					{
						MissionState.getInstance().setState(MState.REJECTED);	
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}
		
		publish(requestTopic, "");
		
	}
	
	private void unsubscribe(String topic) {
		try {
			mqttClient.unsubscribe(topic).waitForCompletion();
		} catch (MqttException e) {
			LOG.warn("Failed to unsubscribe from topic {} with {}!", e);
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

	@Override
	public List<String> getSymbolicPositions() {
		// TODO
		return null;
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
		
		MissionState.getInstance().setState(MState.ACCEPTED);		
		WorkState.getInstance().removeStateListeners();
		PhysicalState.getInstance().removeStateListeners();
	}

	@Override
	public List<String> detectObstacles(String type) {
		MissionState.getInstance().setState(MState.EXECUTING);		
		// TODO Retrieve set of detected obstacles by PS from obstacle detection service
		List<String> result = Collections.emptyList();
		sleep(5000);
		MissionState.getInstance().setState(MState.DONE);		
		return result;
	}
	
	private  void sleep(long millis) {
		try {
			TimeUnit.MILLISECONDS.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void moveToPoint(DronePoint point){
		String requestTopic = "Mavic2/command/moveToPoint/req";
		String responseTopic = "Mavic2/command/moveToPoint/res";

		WorkState.getInstance().addStateListener(new WorkStateListener() {

			@Override
			public void stateChangedEvent(WState oldState, WState newState) {

				if (newState.equals(WState.PHASE_IDLE)) {
					MissionState.getInstance().setState(MState.DONE);		
				} else if (newState.equals(WState.PHASE0)) {
					MissionState.getInstance().setState(MState.EXECUTING);		
				} else if (newState.equals(WState.PHASE1)) {
					MissionState.getInstance().setState(MState.EXECUTING);	
				} else if (newState.equals(WState.PHASE2)) {
					MissionState.getInstance().setState(MState.EXECUTING);	
				}
			}
				
		});
		
		try {
			mqttClient.subscribe(responseTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sResponse = new String(message.getPayload());
					LOG.debug("Got response " + sResponse);
					
					if (sResponse.contains("Accepted")) {
						MissionState.getInstance().setState(MState.ACCEPTED);			
					}
					else // Rejected
					{
						MissionState.getInstance().setState(MState.REJECTED);	
					}
						
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", responseTopic, e);
		}
		
		JsonObject position = Json.createObjectBuilder()
				.add("pos", 
						Json.createObjectBuilder()
						.add("x", point.getPosition().getX())
						.add("y", point.getPosition().getY())
						.add("z", point.getPosition().getZ())
						.build()
						)
				.add("rot", point.getRotation())
				.add("pitch", point.getPitch())
				.build();

		publish(requestTopic, position.toString());
		
	}

	@Override
	public void moveToWaypoints(List<DronePoint> waypoints) {
		// TODO Auto-generated method stub
		
	}
}
