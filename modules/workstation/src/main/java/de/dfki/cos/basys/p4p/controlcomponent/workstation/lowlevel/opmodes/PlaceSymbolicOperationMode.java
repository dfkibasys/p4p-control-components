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
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service.WorkstationStatus;

@OperationMode(name = "PlaceSymbolic", shortName = "PLACESYM", description = "Place objects of specified type and amount at a specified target location",
        allowedCommands = {ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP},
        allowedModes = { ExecutionMode.AUTO, ExecutionMode.SIMULATE })
public class PlaceSymbolicOperationMode extends BaseWorkstationOperationMode {

    @Parameter(name = "pps_workstep_id", direction = ParameterDirection.IN)
    private String workstepId = "";
    @Parameter(name = "place_material", direction = ParameterDirection.IN)
    private String material = "";
    @Parameter(name = "place_quantity", direction = ParameterDirection.IN)
    private int quantity = 1;
    @Parameter(name = "place_target_location", direction = ParameterDirection.IN)
    private String target_location = "";
    @Parameter(name = "place_quantity_placed", direction = ParameterDirection.OUT)
    private int quantity_placed = 0;

    public PlaceSymbolicOperationMode(BaseControlComponent<WorkstationService> component) {
        super(component);
    }

    @Override
    public void onStarting() {
        super.onStarting();
        executing = true;
        getService(WorkstationService.class).placeSymbolic(workstepId);
    }

    @Override
    public void onCompleting() {
        super.onCompleting();
        WorkstationServiceImpl.currentOpMode = WorkstationStatus.OPMode.NONE;
        WorkstationServiceImpl.expected_workstep_id = "";
        WorkstationServiceImpl.current_workstep_id = "-1";
    }

    @Override
    public void onStopping() {
        super.onStopping();
        WorkstationServiceImpl.currentOpMode = WorkstationStatus.OPMode.NONE;
        WorkstationServiceImpl.expected_workstep_id = "";
        WorkstationServiceImpl.current_workstep_id = "-1";
    }
}
