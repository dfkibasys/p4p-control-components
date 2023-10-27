package de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class WorkstationServiceImpl implements WorkstationService, ServiceProvider<WorkstationService> {
    private Properties config = null;
    protected final Logger LOGGER = LoggerFactory.getLogger(WorkstationServiceImpl.class.getName());
    private boolean connected = false;
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
    public void pickSymbolic(String mat_location, int quantity) {
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
}
