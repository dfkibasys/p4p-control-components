package de.dfki.cos.basys.p4p.controlcomponent.workstation.service;

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
    public void reset() {

    }
}
