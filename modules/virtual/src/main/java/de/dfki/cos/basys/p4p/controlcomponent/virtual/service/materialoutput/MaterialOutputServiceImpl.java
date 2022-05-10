package de.dfki.cos.basys.p4p.controlcomponent.virtual.service.materialoutput;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;

public class MaterialOutputServiceImpl implements MaterialOutputService, ServiceProvider<MaterialOutputService> {
    @Override
    public boolean connect(ComponentContext context, String connectionString) {
        return true;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public MaterialOutputService getService() {
        return this;
    }
}
