package de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.opmodes;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.opmodes.BaseWorkstationOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service.WorkstationService;

@OperationMode(name = "PickSymbolic", shortName = "PICK_SYM", description = "Pick objects of specified type and amount from its current location",
        allowedCommands = {ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP},
        allowedModes = { ExecutionMode.AUTO, ExecutionMode.SIMULATE })
public class PickSymbolicOperationMode extends BaseWorkstationOperationMode {

    @Parameter(name = "pick_mat_location", direction = ParameterDirection.IN)
    private String mat_location = "";
    @Parameter(name = "pick_quantity", direction = ParameterDirection.IN)
    private int quantity = 1;
    @Parameter(name = "pick_quantity_taken", direction = ParameterDirection.OUT)
    private int quantity_taken = 0;

    public PickSymbolicOperationMode(BaseControlComponent<WorkstationService> component) {
        super(component);
    }

    @Override
    public void onStarting() {
        super.onStarting();
        getService(WorkstationService.class).pickSymbolic(mat_location, quantity);
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
