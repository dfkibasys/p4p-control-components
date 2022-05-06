package de.dfki.cos.basys.p4p.controlcomponent.vacuum.service;


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

import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.VacuumStatus.*;
import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.dto.StatusEntity;


public class VacuumServiceImpl implements VacuumService, ServiceProvider<VacuumService>{
	private static final Logger LOG = LoggerFactory.getLogger(VacuumServiceImpl.class);
	private static final String PREFIX = "MqttAsyncClient-paho-v3";
	private static final Integer QOS = 0;
	IMqttAsyncClient mqttClient = null;
	String clientId = null;
	
	public VacuumServiceImpl() {
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
					
					// Subscribe to vacuum work state
					String workStateTopic = "rocky/VacuumStatus";
					try {
						mqttClient.subscribe(workStateTopic, QOS, new IMqttMessageListener() {
							
							@Override
							public void messageArrived(String topic, MqttMessage message) throws Exception {
								String sMessage = new String(message.getPayload());	
								
								StatusEntity status = new ObjectMapper().readValue(sMessage, StatusEntity.class);
								
								//TODO: Set "status.state" or "status.state_code" as WState
								//WorkState.getInstance().setState(status.state);

							}
						}).waitForCompletion();
					} catch (MqttException e) {
						LOG.warn(clientId + " could not subscribe to topic {}!", workStateTopic);		
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
	public void moveToPoint(Vector2d point){
		String requestTopic = "rocky/GoToLocationCapability/go/set";
	
		WorkState.getInstance().addStateListener(new WorkStateListener() {
	
			@Override
			public void stateChangedEvent(WState oldState, WState newState) {
				
				/*
				 * check for WState here and update MState
				 * 
				if (newState.equals(WState.RETURNING)) {
					MissionState.getInstance().setState(MState.DONE);		
				} else if (newState.equals(WState.CLEANING)) {
					MissionState.getInstance().setState(MState.EXECUTING);		
				} 
				*/
			}
				
		});
		
		JsonObject position = Json.createObjectBuilder()
				.add("x", point.getX())
				.add("y", point.getY())
				.build();
	
		publish(requestTopic, position.toString());
		
	}

	@Override
	public void reset() {
		//unsubscribe("rocky/...");
		
		MissionState.getInstance().setState(MState.PENDING);		
		WorkState.getInstance().removeStateListeners();
		PhysicalState.getInstance().removeStateListeners();
	}

	@Override
	public void pause() {
		String requestTopic = "rocky/BasicControlCapability/operation/set";

		//check for WState here to see rejection/acceptance and set missionState
		
		publish(requestTopic, "PAUSE");
	}

	@Override
	public void resume() {
		String requestTopic = "rocky/BasicControlCapability/operation/set";

		//check for WState here to see rejection/acceptance and set missionState
		
		publish(requestTopic, "START");
	}

	@Override
	public void abort() {
		String requestTopic = "rocky/BasicControlCapability/operation/set";

		//check for WState here to see rejection/acceptance and set missionState
		
		publish(requestTopic, "STOP");
	}

	@Override
	public boolean isConnected() {
		return mqttClient == null ? false : mqttClient.isConnected();
	}

	@Override
	public VacuumService getService() {
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

	private  void sleep(long millis) {
		try {
			TimeUnit.MILLISECONDS.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			LOG.warn("Failed to unsubscribe from topic {} with {}!", e);
		}
	}
}
