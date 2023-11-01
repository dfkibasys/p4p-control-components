package de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.opmodes;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.opmodes.BaseWorkstationOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service.WorkstationService;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service.WorkstationServiceImpl;

@OperationMode(name = "PickSymbolic", shortName = "PICK_SYM", description = "Pick objects of specified type and amount from its current location",
        allowedCommands = {ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP},
        allowedModes = { ExecutionMode.AUTO, ExecutionMode.SIMULATE })
public class PickSymbolicOperationMode extends BaseWorkstationOperationMode {

    @Parameter(name = "pps_material", direction = ParameterDirection.IN)
    private String material = "";
    @Parameter(name = "pps_mat_location", direction = ParameterDirection.IN)
    private String mat_location = "";
    @Parameter(name = "pps_quantity", direction = ParameterDirection.IN)
    private long quantity = 1;
    @Parameter(name = "pick_quantity_taken", direction = ParameterDirection.OUT)
    private int quantity_taken = 0;

    public PickSymbolicOperationMode(BaseControlComponent<WorkstationService> component) {
        super(component);
    }

    @Override
    public void onStarting() {
        super.onStarting();
        executing = true;
        getService(WorkstationService.class).pickSymbolic(material, mat_location, (int) quantity);
    }

    @Override
    public void onCompleting() {
        super.onCompleting();
        // reset variables
        WorkstationServiceImpl.expected_quantity = 1;
        WorkstationServiceImpl.current_quantity = 0;
        WorkstationServiceImpl.expected_mat_location = "";
        WorkstationServiceImpl.expected_material = "";
    }

    @Override
    public void onStopping() {
        super.onStopping();
        // reset variables
        WorkstationServiceImpl.expected_quantity = 1;
        WorkstationServiceImpl.current_quantity = 0;
        WorkstationServiceImpl.expected_mat_location = "";
        WorkstationServiceImpl.expected_material = "";
    }
}
