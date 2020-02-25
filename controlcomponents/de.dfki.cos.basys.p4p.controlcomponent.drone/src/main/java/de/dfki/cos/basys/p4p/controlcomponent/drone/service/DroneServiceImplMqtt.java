package de.dfki.cos.basys.p4p.controlcomponent.drone;

import java.util.List;
import java.util.UUID;

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
		return mqttClient.isConnected();
	}

	@Override
	public DroneService getService() {
		return this;
	}


	@Override
	public void moveToSymbolicPosition(String position) {
		String stateTopic = "Mavic2/state/moveToLocation";
		String commandTopic = "Mavic2/command/moveToLocation";
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());				
					LOG.debug("New message arrived " + sMessage);
					if (sMessage.contains("Phase Idle")) {
						missionState = "done";
					} else if (sMessage.contains("Phase 0")) {
						missionState = "pending";
					} else if (sMessage.contains("Phase 1")) {
						missionState = "executing";
					} else if (sMessage.contains("Phase 2")) {
						missionState = "executing";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}

		publish(commandTopic, "{\"position\": \"" + position + "\"}");

	}

	@Override
	public void takeOff() {
		String stateTopic = "Mavic2/state/takeOffAndHandOverControl";
		String commandTopic = "Mavic2/command/takeOffAndHandOverControl";
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("handover done")) {
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
		moveToSymbolicPosition("Drone-Home");
	}

	@Override
	public String getMissionState() {
		return this.missionState;
	}

	@Override
	public String getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startVideoStream() {
		String stateTopic = "Mavic2/command/videoStreaming";
		String commandTopic = "Mavic2/state/videoStreaming";
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("starting done")) {
						missionState = "done";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}
		
		publish(commandTopic, "{\"action\": \"start\"}");
	}

	@Override
	public void stopVideoStream() {
		String stateTopic = "Mavic2/command/videoStreaming";
		String commandTopic = "Mavic2/state/videoStreaming";
		try {
			mqttClient.subscribe(stateTopic, QOS, new IMqttMessageListener() {
				
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String sMessage = new String(message.getPayload());
					if (sMessage.contains("stopping done")) {
						missionState = "done";
					}

					
				}
			}).waitForCompletion();
		} catch (MqttException e) {
			LOG.error("Failed to subscribe to topic {} with {}.", stateTopic, e);
		}
		
		publish(commandTopic, "{\"action\": \"stop\"}");

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
		unsubscribe("Mavic2/state/moveToLocation");
		unsubscribe("Mavic2/state/videoStreaming");
		unsubscribe("Mavic2/state/takeOffAndHandOverControl");
			
		missionState = "pending";
	}
	
}
