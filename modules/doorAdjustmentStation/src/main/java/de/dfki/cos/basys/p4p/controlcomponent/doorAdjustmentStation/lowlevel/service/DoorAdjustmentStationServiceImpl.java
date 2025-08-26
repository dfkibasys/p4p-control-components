package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.dto.InstructionResponse;
import de.dfki.cos.basys.processcontrol.model.*;
import de.dfki.cos.mrk40.avro.JointStateStamped;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
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
    String clientId = null;

    @Autowired
    StreamBridge streamBridge;

    public DoorAdjustmentStationServiceImpl(Properties config) {
        clientId = PREFIX + UUID.randomUUID().toString();
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
            LOGGER.error("Generation of MqttAsyncClient failed wih {}!", e);
            return false;
        }

        try {
            mqttClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LOGGER.debug(clientId + " successfully connected to {}.", connectionString);

                    String instructionTopic = "/aiquama/obeyInstruction/response";
                    try {
                        mqttClient.subscribe(instructionTopic, QOS, (topic, message) -> {
                            handleMQTTDoorUpdates(message);
                        });
                    } catch (MqttException e) {
                        LOGGER.warn(clientId + " could not subscribe to topic {}!", instructionTopic);
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LOGGER.warn(clientId + " could not establish connection to {}!", connectionString);
                }
            }).waitForCompletion();
        } catch (MqttException e) {
            LOGGER.error("Establishing connection to {} failed with {}!", connectionString, e);
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

        if (currentTask != null) {
            MissionState.getInstance().setState(MState.EXECUTING);

            //TODO: Send instruction via MQTT

            // Wait for response?
            MissionState.getInstance().setState(MState.DONE);
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

    private void handleMQTTDoorUpdates(MqttMessage message) {
        //Only evaluate in OBEY opMode
        if (currentOpMode != OPMode.OBEY) return;

        String payload = new String(message.getPayload());

        try {
            InstructionResponse data = objectMapper.readValue(payload, InstructionResponse.class);
            LOGGER.info("Parsed object: " + data);

            if (currentTask.equals(TASK.ADJUST_DOOR) &&
                    data.taskId.equals(TASK.ADJUST_DOOR) &&
                    data.success) {
                latch.countDown();
            }

        } catch (Exception e) {
            LOGGER.error("Failed to parse JSON: " + e.getMessage());
        }
    }

    private void sendNotification(NotificationType type, boolean show) {
        Notification not = new Notification();
        not.setType(type);
        not.setShow(show);
        streamBridge.send("notification", not);
    }

    public static TASK convertStringToEnum(String taskString) {
        try {
            return TASK.valueOf(taskString);
        } catch (IllegalArgumentException e) {
            // Handle the case when the input String doesn't match any enum constant
            System.out.println("Invalid task string: " + taskString);
            return null;
        }
    }
}
