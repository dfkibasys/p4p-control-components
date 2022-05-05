package de.dfki.cos.basys.p4p.controlcomponent.virtual.opmodes.gate;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.virtual.service.gate.GateService;

@OperationMode(name = "OpenGate", shortName = "OPENGT", description = "Opens the gate",
        allowedCommands = { ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP },
        allowedModes = { ExecutionMode.SIMULATE })
public class OpenOperationMode extends BaseOperationMode<GateService> {

    public OpenOperationMode(BaseControlComponent<GateService> component) {
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
