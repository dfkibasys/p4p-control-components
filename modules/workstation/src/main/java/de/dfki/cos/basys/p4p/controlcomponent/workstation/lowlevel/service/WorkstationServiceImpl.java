package de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.model.MaterialRemovedEvent;
import de.dfki.cos.basys.processcontrol.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service.WorkstationStatus.MState;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class WorkstationServiceImpl implements WorkstationService, ServiceProvider<WorkstationService> {

    public static int expected_quantity = 1;
    public static int current_quantity = 0;
    public static String expected_mat_location = "";
    public static String expected_material = "";
    private Properties config = null;
    protected final Logger LOGGER = LoggerFactory.getLogger(WorkstationServiceImpl.class.getName());
    private boolean connected = false;

    @Autowired
    StreamBridge streamBridge;

    public WorkstationServiceImpl(Properties config) {
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
    public WorkstationService getService() {
        return this;
    }

    @Override
    public void pickSymbolic(String material, String mat_location, int quantity) {
        // e.g. mat_location: box_01 | quantity: 4
        // onStarting
        // Find validation services / sensors (Logitech camera, scale)
        // Subscribe to respective events
        // Scale: Kafka topic combining prefix + mat_location, e.g. 'scale-box_01'
        // Camera: Two Topics (e.g. 'hand-direction' / 'hand-grasping') sending direction intention and grasping of hands (e.g. right_hand: box_01)

        // on Execute
        /*
         * - react to events and decide
         * - wrong direction -> hint in WGS (POST http://wgs-management-service/notify/direction) or Kafka -> payload?
         * - wrong location reached -> hint in WGS (POST http://wgs-management-service/notify/location)
         * - reached into wrong location -> hint in WGS (POST http://wgs-management-service/notify/grasping)
         * - wrong quantity withdrew -> hint in WGS (POST http://wgs-management-service/notify/quantity)
         * - combinations: wrong location, correct quantity / correct location, wrong quantity (see above)
         * - ?mat_location reached && withdrawal of ?quantity ?material  -> complete (clear hints)
         * - ?mat_location reached && withdrawal of <?quantity && hand retrackted
         */
        expected_mat_location = mat_location;
        expected_quantity = quantity;
        expected_material = material;

        MissionState.getInstance().setState(MState.EXECUTING);

        while (expected_quantity != current_quantity) {
            try {
                LOGGER.info("Expected: {}, Current: {}", expected_quantity, current_quantity);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        MissionState.getInstance().setState(MState.DONE);
    }

    @Override
    public void placeSymbolic(String material, int quantity, String target_location) {
        // onStarting
        // Find validation services / sensors (Logitech camera, Flir camera)
        // Subscribe to respective events
    }

    @Override
    public void joinSymbolic(String material_a, String material_b, String tool) {

    }

    @Override
    public void reset() {

    }

    @Override
    public MissionState getMissionState() {
        return MissionState.getInstance();
    }

    @Bean
    public Consumer<AssemblyEvent> assemblyEventUpdates() {
        return this::handleAssemblyEventUpdates;
    }

    @Bean
    public Consumer<HandEvent> handEventUpdates() {
        return this::handleHandEventUpdates;
    }

    @Bean
    public Consumer<MaterialRemovedEvent> scaleController0Updates() { return this::handleScaleControllerUpdates;}
    @Bean
    public Consumer<MaterialRemovedEvent> scaleController1Updates() { return this::handleScaleControllerUpdates;}
    @Bean
    public Consumer<MaterialRemovedEvent> scaleController2Updates() { return this::handleScaleControllerUpdates;}
    @Bean
    public Consumer<MaterialRemovedEvent> scaleController3Updates() { return this::handleScaleControllerUpdates;}
    @Bean
    public Consumer<MaterialRemovedEvent> scaleController4Updates() { return this::handleScaleControllerUpdates;}

    private void handleAssemblyEventUpdates(AssemblyEvent assemblyEvent) {
        LOGGER.info("Assembly Event arrived {}", assemblyEvent);
    }

    private void handleHandEventUpdates(HandEvent handEvent) {
        LOGGER.info("Hand Event arrived {}", handEvent);
        Notification not = new Notification();

        switch (handEvent.getType()) {
            case LEADING_INTO_DIRECTION:
                not.setType(NotificationType.LEADING_INTO_WRONG_DIRECTION);
                break;
            case LOCATION_REACHED:
                not.setType(NotificationType.WRONG_LOCATION_REACHED);
                break;
            case GRASPED_AT_LOCATION:
                not.setType(NotificationType.GRASPED_AT_WRONG_LOCATION);
                break;
        }

        not.setShow(!Objects.equals(handEvent.getTarget(), expected_mat_location));
        streamBridge.send("notification", not);
    }

    private void handleScaleControllerUpdates(MaterialRemovedEvent materialRemovedEvent) {
        // Skip validation for other scales
        if (!Objects.equals(expected_material, materialRemovedEvent.getMaterial())) return;

        LOGGER.info("Material Removed Event arrived {}", materialRemovedEvent);
        current_quantity -= materialRemovedEvent.getRemoved(); //amount for taken material is negative, so deduct from current quantity
        Notification not = new Notification();
        not.setType(NotificationType.WRONG_QUANTITY_TAKEN);
        not.setShow(current_quantity != expected_quantity);
        streamBridge.send("notification", not);
    }
}
