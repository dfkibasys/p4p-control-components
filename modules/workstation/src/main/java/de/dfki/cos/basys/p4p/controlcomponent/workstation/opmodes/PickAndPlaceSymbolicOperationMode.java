package de.dfki.cos.basys.p4p.controlcomponent.workstation.opmodes;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.service.WorkstationService;

@OperationMode(name = "PickAndPlaceSymbolic", shortName = "PP_SYM", description = "Pick and place an object of specified type from its current location" +
        "to a symbolic target location",
        allowedCommands = {ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP},
        allowedModes = { ExecutionMode.AUTO, ExecutionMode.SIMULATE })
public class PickAndPlaceSymbolicOperationMode extends BaseWorkstationOperationMode {

    @Parameter(name = "pps_material", direction = ParameterDirection.IN)
    private String material = "";
    @Parameter(name = "pps_quantity", direction = ParameterDirection.IN)
    private int quantity = 1;
    @Parameter(name = "pps_target_location", direction = ParameterDirection.IN)
    private String target_location = "";
    @Parameter(name = "pps_workstep_id", direction = ParameterDirection.IN)
    private String workstep_id = "";

    public PickAndPlaceSymbolicOperationMode(BaseControlComponent<WorkstationService> component) {
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
