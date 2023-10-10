package de.dfki.cos.basys.p4p.controlcomponent.workstation.opmodes;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.service.WorkstationService;

@OperationMode(name = "JoinSymbolic", shortName = "JOIN_SYM", description = "Join a picked object of specified type to a second object of specified type" +
        "with a specified tool.",
        allowedCommands = {ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP},
        allowedModes = { ExecutionMode.AUTO, ExecutionMode.SIMULATE })
public class JoinSymbolicOperationMode extends BaseWorkstationOperationMode {

    @Parameter(name = "join_material_a", direction = ParameterDirection.IN)
    private String material_a = "";
    @Parameter(name = "join_material_b", direction = ParameterDirection.IN)
    private String material_b = "";
    @Parameter(name = "join_tool", direction = ParameterDirection.IN)
    private String tool = "";

    public JoinSymbolicOperationMode(BaseControlComponent<WorkstationService> component) {
        super(component);
    }

    @Override
    public void onStarting() {
        super.onStarting();
    }

    @Override
    public void onCompleting() {
        super.onCompleting();
    }

    @Override
    public void onStopping() {
        super.onStopping();
    }
}
