package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.dto.*;
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

import java.util.List;
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
    private final float CONFIDENCE_THRESHOLD = 0.9F;
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
        options.setUserName("DAS-ControlComponent");
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

                    String instructionResponseTopic = "/aiquama/showInstruction/response";
                    String clipResponseTopic = "/SiMRK4.0/command/showVideoClip/res";
                    String cameraResponseTopic = "/aiquama/camera/status";
                    try {
                        mqttClient.subscribe(instructionResponseTopic, QOS, (topic, message) -> {
                            handleMQTTInstructionResponses(message);
                        });
                        mqttClient.subscribe(clipResponseTopic, QOS, (topic, message) -> {
                            handleClipResponse(message);
                        });
                        mqttClient.subscribe(cameraResponseTopic, QOS, (topic, message) -> {
                            handleCameraResponse(message);
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

            // CHECK_DOOR is the only task that needs to be requested
            if (currentTask.equals(TASK.CHECK_DOOR)) {
                ShowVideoClipRequest req = ShowVideoClipRequest.builder().clip_id("Check_door.mp4").position_id("tv").issuer_id("aiquama_process").build();
                String jsonPayload = null;
                try {
                    jsonPayload = objectMapper.writeValueAsString(req);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                publish("/SiMRK4.0/command/showVideoClip/req", jsonPayload);
            }

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
    }

    private void handleMQTTInstructionResponses(MqttMessage message) {
        String payload = new String(message.getPayload());

        try {
            InstructionResponse response = objectMapper.readValue(payload, InstructionResponse.class);
            LOGGER.info("Parsed object: {}", response);

            if (currentOpMode.equals(OPMode.SHOW) && currentTask.equals(response.getTaskId()) && response.getSuccess()) {
                latch.countDown();
            }

        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to parse JSON: {}", e.getMessage());
        }
    }

    private void handleCameraResponse(MqttMessage message) {
        String payload = new String(message.getPayload());

        try {
            List<StateConfidence> states = objectMapper.readValue(payload, new TypeReference<>() {});
            LOGGER.info("Parsed object: {}", states);
            boolean hasCurrentTaskWithHighConfidence = states.stream()
                    .anyMatch(sc -> currentTask.name().equals(sc.getState()) && sc.getConfidence() >= CONFIDENCE_THRESHOLD);

            if (currentOpMode.equals(OPMode.OBEY) && hasCurrentTaskWithHighConfidence) {
                latch.countDown();
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to parse JSON: {}", e.getMessage());
        }
    }

    private void handleClipResponse(MqttMessage message) {
        String payload = new String(message.getPayload());

        try {
            ShowVideoClipResponse response = objectMapper.readValue(payload, ShowVideoClipResponse.class);
            LOGGER.info("Parsed object: {}", response);

            if (currentOpMode.equals(OPMode.OBEY) && currentTask.equals(TASK.CHECK_DOOR) && response.getStatus().equals("DONE")){
                latch.countDown();
            }
        } catch (JsonProcessingException e) {
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
