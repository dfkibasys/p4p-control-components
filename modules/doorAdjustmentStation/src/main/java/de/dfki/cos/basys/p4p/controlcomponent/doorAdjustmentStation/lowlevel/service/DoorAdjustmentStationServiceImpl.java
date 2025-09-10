package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.dto.InstructionResponse;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.dto.ShowInstructionRequest;
import de.dfki.cos.mrk40.avro.JointStateStamped;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationStatus.*;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@Service
public class DoorAdjustmentStationServiceImpl implements DoorAdjustmentStationService, ServiceProvider<DoorAdjustmentStationService> {

    static CountDownLatch latch;
    public static OPMode currentOpMode = OPMode.NONE;
    public static TASK currentTask = TASK.NONE;
    private Properties config = null;
    private final float DOOR_ANGLE_OPENED = 90.0F;
    private final float DOOR_ANGLE_CLOSED = 10.0F;
    protected final Logger LOGGER = LoggerFactory.getLogger(DoorAdjustmentStationServiceImpl.class.getName());
    private boolean connected = false;
    private static final String PREFIX = "MqttAsyncClient-paho-v3";
    private static final Integer QOS = 0;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    IMqttAsyncClient mqttClient = null;
    String clientId;

    public DoorAdjustmentStationServiceImpl(Properties config) {
        clientId = PREFIX + UUID.randomUUID();
        this.config = config;
    }

    @Override
    public boolean connect(ComponentContext context, String connectionString) {
        MemoryPersistence persistence = new MemoryPersistence();
        final MqttConnectOptions options = new MqttConnectOptions();

        options.setCleanSession(true);
        try {
            mqttClient = new MqttAsyncClient(connectionString, clientId, persistence);
        } catch (MqttException e) {
            LOGGER.error("Generation of MqttAsyncClient failed with {}!", e.getMessage());
            return false;
        }

        try {
            mqttClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LOGGER.debug("{} successfully connected to {}.", clientId, connectionString);

                    String responseTopics = "/aiquama/+/response";
                    try {
                        mqttClient.subscribe(responseTopics, QOS, (topic, message) -> {
                            handleMQTTResponses(message);
                        });
                    } catch (MqttException e) {
                        LOGGER.warn("{} could not subscribe to every topic!", clientId);
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LOGGER.warn("{} could not establish connection to {}!", clientId, connectionString);
                }
            }).waitForCompletion();
        } catch (MqttException e) {
            LOGGER.error("Establishing connection to {} failed with {}!", connectionString, e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public DoorAdjustmentStationService getService() {
        return this;
    }

    @Override
    public void obey(String taskId) {
        currentOpMode = OPMode.OBEY;
        currentTask = convertStringToEnum(taskId);

        if (currentTask != null) {
            MissionState.getInstance().setState(MState.EXECUTING);

            latch = new CountDownLatch(1);

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            MissionState.getInstance().setState(MState.DONE);
        }
    }

    @Override
    public void show(String taskId) {
        currentOpMode = OPMode.SHOW;
        currentTask = convertStringToEnum(taskId);

        if (currentTask != null) { // check if existing in enum
            MissionState.getInstance().setState(MState.EXECUTING);
            try {
                String instructionTopic = "/aiquama/showInstruction/request";

                ShowInstructionRequest sir = InstructionSettings.getInstance().getInstruction(taskId); // access with String
                String jsonPayload = objectMapper.writeValueAsString(sir);
                publish(instructionTopic, jsonPayload);

                latch = new CountDownLatch(1);

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                MissionState.getInstance().setState(MState.DONE);
            } catch (Exception e) {
                LOGGER.error("Failed to process JSON: {}", e.getMessage());
            }
        }
    }

    @Override
    public void reset() {
        publish("/aiquama/showInstruction/reset", "");
    }

    @Override
    public MissionState getMissionState() {
        return MissionState.getInstance();
    }

    @Bean
    @ConditionalOnProperty(prefix = "basys.controlcomponent", name = "kafka-enabled", havingValue = "true")
    public Consumer<JointStateStamped> doorEventUpdates() {
        return this::handleDoorUpdates;
    }

    private void handleDoorUpdates(JointStateStamped jointStateStamped) {
        //Only evaluate in OBEY opMode
        if (currentOpMode != OPMode.OBEY) return;

        Float doorAngle = jointStateStamped.getState().getPosition().get(0);

        LOGGER.info("Front Door Left Event arrived {}", doorAngle);

        if (currentTask.equals(TASK.OPEN_DOOR) && doorAngle > DOOR_ANGLE_OPENED) {
            latch.countDown();
        } else if (currentTask.equals(TASK.CLOSE_DOOR) && doorAngle < DOOR_ANGLE_CLOSED) {
            latch.countDown();
        }

    }

    private void handleMQTTResponses(MqttMessage message) {
        String payload = new String(message.getPayload());

        try {
            InstructionResponse response = objectMapper.readValue(payload, InstructionResponse.class);
            LOGGER.info("Parsed object: {}", response);

            // TODO: Guarantee that an OBEY response is not quitting a SHOW opMode or vice versa
            if (!currentOpMode.equals(OPMode.NONE) && currentTask.equals(response.getTaskId()) && response.getSuccess()) {
                latch.countDown();
            }

        } catch (Exception e) {
            LOGGER.error("Failed to parse JSON: {}", e.getMessage());
        }
    }

    private TASK convertStringToEnum(String taskString) {
        try {
            return TASK.valueOf(taskString);
        } catch (IllegalArgumentException e) {
            // Handle the case when the input String doesn't match any enum constant
            LOGGER.error("Invalid task string: {}", taskString);
            return null;
        }
    }

    private void publish(String topic, String content) {
        final MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(QOS);
        message.setRetained(false);
        try {
            mqttClient.publish(topic, message).waitForCompletion();
            LOGGER.debug("publishing message {} on topic {}", message, topic);
        } catch (MqttException e) {
            LOGGER.error("Failed to publish message {} on topic {} with {}", message, topic, e.getMessage());
        }
    }
}
