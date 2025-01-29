package de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service;


import java.util.UUID;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.SmartwatchStatus.*;

import javax.json.Json;
import javax.json.JsonObject;


public class SmartwatchMQTTServiceImpl implements de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.SmartwatchService, ServiceProvider<de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.SmartwatchService>{

	private Properties config = null;
	private static final Logger LOG = LoggerFactory.getLogger(SmartwatchMQTTServiceImpl.class);
	private static final String PREFIX = "MqttAsyncClient-paho-v3";
	private static final Integer QOS = 2;
	IMqttAsyncClient mqttClient = null;
	String clientId = null;
	String topicTaskRequest, topicTaskStatus, topicNotification = null;

	String currentTaskId="";


	public SmartwatchMQTTServiceImpl(Properties config) {
		clientId = PREFIX + UUID.randomUUID().toString();
		this.config = config;
	}
	
	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		MemoryPersistence persistence = new MemoryPersistence();
		final MqttConnectOptions options = new MqttConnectOptions();

		//options.setUserName(config.getProperty("mqttUsername"));
		//options.setPassword(config.getProperty("mqttPassword").toCharArray());
		options.setCleanSession(true);
		topicNotification = config.getProperty("topicNotification");
		topicTaskRequest = config.getProperty("topicTaskRequest");
		topicTaskStatus = config.getProperty("topicTaskStatus");
		try {
			mqttClient = new MqttAsyncClient(connectionString, clientId, persistence);
		} catch (MqttException e) {
			LOG.error("Generation of MqttAsyncClient failed wih {}!", e);
			return false;
		}
		
		try {
			mqttClient.connect(options, null, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					LOG.debug(clientId + " successfully connected to {}.", connectionString);	
					
					// Subscribe task states
					try {
						mqttClient.subscribe(topicTaskStatus, QOS, (topic, message) -> {
							String sMessage = new String(message.getPayload());
							TaskStatus ts = null;
							try {
								ts = new ObjectMapper().readValue(sMessage, new TypeReference<TaskStatus>() {});
							} catch (JsonProcessingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(!ts.taskId.equals(currentTaskId))
								return;

							switch(ts.taskStatus) {
								case "PENDING":
									TaskState.getInstance().setState(TState.PENDING);
									break;
								case "ACCEPTED":
									TaskState.getInstance().setState(TState.ACCEPTED);
									break;
								case "REJECTED":
									TaskState.getInstance().setState(TState.REJECTED);
									break;
								case "EXECUTING":
									TaskState.getInstance().setState(TState.EXECUTING);
									break;
								case "PAUSED":
									TaskState.getInstance().setState(TState.PAUSED);
									break;
								case "CANCELED":
									TaskState.getInstance().setState(TState.CANCELED);
									break;
								case "FAILED":
									TaskState.getInstance().setState(TState.FAILED);
									break;
								case "DONE":
									TaskState.getInstance().setState(TState.DONE);
									break;
								default:
									LOG.warn("Received unexpected task state {}! Ignoring.", ts.taskStatus);
							}

						}).waitForCompletion();
					} catch (MqttException e) {
						LOG.warn(clientId + " could not subscribe to topic {}!", topicTaskStatus);
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
	public TaskState getTaskState() {
		return TaskState.getInstance();
	}

	@Override
	public void requestTaskExecution(TaskRequest request) {

		currentTaskId = request.taskId;

		JsonObject payloadJson = Json.createObjectBuilder()
			.add("taskDescription", request.taskDescription)
			.add("taskTitle", request.taskTitle)
			.add("taskId", request.taskId)
			.build();

		publish(topicTaskRequest, payloadJson.toString());
	}

	@Override
	public void displayInfoMessage(Notification message) {
		JsonObject payloadJson = Json.createObjectBuilder()
				.add("infoText", message.infoText)
				.add("infoTitle", message.infoTitle)
				.build();

		publish(topicNotification, payloadJson.toString());
		TaskState.getInstance().setState(TState.DONE);
	}

	@Override
	public void reset() {
		// TODO: What should happen on smartwatch device?
		currentTaskId = "";
		TaskState.getInstance().setState(TState.NONE);
	}

	@Override
	public boolean isConnected() {
		return mqttClient != null && mqttClient.isConnected();
	}

	@Override
	public de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.SmartwatchService getService() {
		return this;
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
