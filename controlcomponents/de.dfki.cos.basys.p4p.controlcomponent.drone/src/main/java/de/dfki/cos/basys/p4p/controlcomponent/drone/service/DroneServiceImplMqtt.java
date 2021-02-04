package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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


public class DroneServiceImplMqtt implements DroneService, ServiceProvider<DroneService>{
	private static final Logger LOG = LoggerFactory.getLogger(DroneServiceImplMqtt.class);
	private static final String PREFIX = "MqttAsyncClient-paho-v3";
	private static final Integer QOS = 0;
	String missionState = "pending";
	String workState = "";
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
	public void moveToSymbolicPosition(String position) {
		String stateTopic = "Mavic2/state/flightPhase";
		String commandTopic = "Mavic2/command/moveToKnownPosition/req";
		workState = "";
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());				
					LOG.debug("New message arrived " + sMessage);
					if (sMessage.contains("Phase Idle")) {
						missionState = "done";
						workState = "Done";
					} else if (sMessage.contains("Phase 0")) {
						missionState = "pending";
						workState = "Phase 0";
					} else if (sMessage.contains("Phase 1")) {
						missionState = "executing";
						workState = "Phase 1";
					} else if (sMessage.contains("Phase 2")) {
						missionState = "executing";
						workState = "Phase 2";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}
		
		if ("_HOME_".equals(position)) {
			position = "Drone-Home";
		}

		publish(commandTopic, "{\"position\": \"" + position + "\"}");

	}
	
	@Override
	public void pause() {
		String stateTopic = "Mavic2/command/pauseMotion/res";
		String commandTopic = "Mavic2/command/pauseMotion/req";
		
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						missionState = "done";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}

		publish(commandTopic, "");
	}
	
	@Override
	public void resume() {
		String stateTopic = "Mavic2/command/continueMotion/res";
		String commandTopic = "Mavic2/command/continueMotion/req";
		
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						missionState = "done";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}

		publish(commandTopic, "");
	}

	@Override
	public void abort() {
		String stateTopic = "Mavic2/command/emergencyLanding/res";
		String commandTopic = "Mavic2/command/emergencyLanding/req";
		
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						missionState = "done";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}

		publish(commandTopic, "from ControlComponent");
	}
	
	@Override
	public void takeOff() {
		String stateTopic = "Mavic2/command/takeOffAndHandOverControl/res";
		String commandTopic = "Mavic2/command/takeOffAndHandOverControl/req";
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						missionState = "done";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}
		
		publish(commandTopic, "");
	}

	@Override
	public void land() {
		// TODO Maybe decouple this from MoveToLocation(Home);
		moveToSymbolicPosition("_HOME_");
	}

	@Override
	public String getMissionState() {
		return this.missionState;
	}

	@Override
	public String getWorkState() {
		return this.workState;
	}
	
	@Override
	public String getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startLiveImage() {
		String stateTopic = "Mavic2/command/startLiveImage/res";
		String commandTopic = "Mavic2/command/startLiveImage/req";
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						missionState = "done";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}
		
		publish(commandTopic, "");
	}

	@Override
	public void stopLiveImage() {
		String stateTopic = "Mavic2/command/stopLiveImage/res";
		String commandTopic = "Mavic2/command/stopLiveImage/req";
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						missionState = "done";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}
		
		publish(commandTopic, "");

	}
	

	@Override
	public void startRTMPStream() {
		String stateTopic = "Mavic2/command/startRTMPStream/res";
		String commandTopic = "Mavic2/command/startRTMPStream/req";
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						missionState = "done";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}
		
		publish(commandTopic, "");
		
	}

	@Override
	public void stopRTMPStream() {
		String stateTopic = "Mavic2/command/stopRTMPStream/res";
		String commandTopic = "Mavic2/command/stopRTMPStream/req";
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("Accepted")) {
						missionState = "done";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}
		
		publish(commandTopic, "");
		
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
		unsubscribe("Mavic2/state/flightPhase");
		unsubscribe("Mavic2/command/startLiveImage/res");
		unsubscribe("Mavic2/command/stopLiveImage/res");
		unsubscribe("Mavic2/command/startRTMPStream/res");
		unsubscribe("Mavic2/command/stopRTMPStream/res");
		unsubscribe("Mavic2/command/takeOffAndHandOverControl/res");
		unsubscribe("Mavic2/command/pauseMotion/res");
		unsubscribe("Mavic2/command/continueMotion/res");
		unsubscribe("Mavic2/command/emergencyLanding/res");
		
		missionState = "pending";
	}

	@Override
	public List<String> detectObstacles(String type) {
		missionState = "executing";
		workState = "detecting obstacles of type " + type + " ...";
		// TODO Retrieve set of detected obstacles by PS from obstacle detection service
		sleep(5000);
		missionState = "done";
		workState = "Done";
		return Collections.emptyList();
	}
	
	private  void sleep(long millis) {
		try {
			TimeUnit.MILLISECONDS.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
