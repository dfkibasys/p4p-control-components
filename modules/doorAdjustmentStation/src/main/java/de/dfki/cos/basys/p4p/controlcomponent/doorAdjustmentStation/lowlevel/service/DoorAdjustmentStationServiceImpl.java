package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.mrk40.avro.JointStateStamped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationStatus.*;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@Service
public class DoorAdjustmentStationServiceImpl implements DoorAdjustmentStationService, ServiceProvider<DoorAdjustmentStationService> {

    static CountDownLatch latch;
    public static OPMode currentOpMode = OPMode.NONE;
    private Properties config = null;
    protected final Logger LOGGER = LoggerFactory.getLogger(DoorAdjustmentStationServiceImpl.class.getName());
    private boolean connected = false;

    @Autowired
    StreamBridge streamBridge;

    public DoorAdjustmentStationServiceImpl(Properties config) {
        this.config = config;
    }

    @Override
    public boolean connect(ComponentContext context, String connectionString) {
        return connected;
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
    public void obey(String workstepId) {
        currentOpMode = OPMode.OBEY;

        MissionState.getInstance().setState(MState.EXECUTING);

        latch = new CountDownLatch(1);

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        MissionState.getInstance().setState(MState.DONE);
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

        LOGGER.info("Front Door Left Event arrived {}", jointStateStamped.getState().getPosition().get(0));

    }

    private void sendNotification(NotificationType type, boolean show) {
        Notification not = new Notification();
        not.setType(type);
        not.setShow(show);
        streamBridge.send("notification", not);
    }
}
