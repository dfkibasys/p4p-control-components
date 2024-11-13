package de.dfki.cos.basys.p4p.controlcomponent.binpicking.service;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Properties;
import java.util.UUID;

public class BinPickingServiceImpl implements BinPickingService, ServiceProvider<BinPickingService> {

    private Properties config = null;
    private static final Logger LOG = LoggerFactory.getLogger(BinPickingServiceImpl.class);
    private static final String PREFIX = "MqttAsyncClient-paho-v3";
    private static final Integer QOS = 0;
    IMqttAsyncClient mqttClient = null;
    String clientId = null;

    public BinPickingServiceImpl(Properties config) {
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

                    // Subscribe to binpicking status updates
                    String workStateTopic = "binpicking/status";
                    try {
                        mqttClient.subscribe(workStateTopic, QOS, (topic, message) -> {
                            String sMessage = new String(message.getPayload());
                            if (sMessage.contains("object detection")) {
                                WorkState.getInstance().setState(BinPickingStatus.WState.DETECTING);
                            } else if (sMessage.contains("robot path planning")) {
                                WorkState.getInstance().setState(BinPickingStatus.WState.PATH_PLANNING);
                            } else if (sMessage.contains("3D pose estimation")) {
                                WorkState.getInstance().setState(BinPickingStatus.WState.POSE_ESTIMATION);
                            } else if (sMessage.contains("pick and place")) {
                                WorkState.getInstance().setState(BinPickingStatus.WState.PICK_AND_PLACE);
                            }
                            else{
                                LOG.warn("Received unknown work state message {}! Ignoring.", sMessage);
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
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    @Override
    public BinPickingService getService() {
        return this;
    }

    @Override
    public void provideParts(String partType, int number, String targetLocation) {
        String responseTopic = "binpicking/command/response";
        String requestTopic = "binpicking/command/request";

        // TODO
//        StringBuilder sb = new StringBuilder("[");
//
//        for (int i = 0; i < number; i++) {
//            sb.append(partType); //sb.append("\"").append(partType).append("\"");
//            if (i < number - 1) {
//                sb.append(", ");
//            }
//        }
//        sb.append("]");
//
//        String payload = sb.toString();
//        //String payload = "[Upper_BackSide, Lower_FrontSide, Lower_BackSide, Upper_FrontSide]";
        JsonObject payloadJson = Json.createObjectBuilder()
                .add("type", partType)
                .add("count", number)
                .add("location", targetLocation)
                .build();

        subscribeToResponse(responseTopic);
        publish(requestTopic, payloadJson.toString());
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
    public void reset() {
        unsubscribe("binpicking/command/response");

        MissionState.getInstance().setState(BinPickingStatus.MState.NONE);
        WorkState.getInstance().removeStateListeners();
    }

    private void subscribeToResponse(String responseTopic){
        try {
            mqttClient.subscribe(responseTopic, QOS, (topic, message) -> {
                String sMessage = new String(message.getPayload());
                if (sMessage.contains("accepted")) {
                    MissionState.getInstance().setState(BinPickingStatus.MState.ACCEPTED);
                }
                else // Rejected
                {
                    MissionState.getInstance().setState(BinPickingStatus.MState.REJECTED);
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
