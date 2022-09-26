package de.dfki.cos.basys.p4p.controlcomponent.virtual.opmodes.trafficlight;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.virtual.service.trafficlight.TrafficLightService;

@OperationMode(name = "SetLights", shortName = "SETLIGHT", description = "Sets the lights of a traffic light",
        allowedCommands = { ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP },
        allowedModes = { ExecutionMode.SIMULATE })
public class SetLightsOperationMode extends BaseOperationMode<TrafficLightService> {

    public SetLightsOperationMode(BaseControlComponent<TrafficLightService> component) {
        super(component);
    }

    @Override
    public void onResetting() {

    }

    @Override
    public void onStarting() {

    }

    @Override
    public void onExecute() {

    }

    @Override
    public void onCompleting() {

    }

    @Override
    public void onStopping() {

    }
}
