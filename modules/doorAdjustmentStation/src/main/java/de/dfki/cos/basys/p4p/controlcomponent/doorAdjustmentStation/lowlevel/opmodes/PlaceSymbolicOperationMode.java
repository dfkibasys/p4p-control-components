package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.opmodes;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationService;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationServiceImpl;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationStatus;

@OperationMode(name = "PlaceSymbolic", shortName = "PLACESYM", description = "Place objects of specified type and amount at a specified target location",
        allowedCommands = {ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP},
        allowedModes = { ExecutionMode.AUTO, ExecutionMode.SIMULATE })
public class PlaceSymbolicOperationMode extends BaseDoorAdjustmentStationOperationMode {

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

    public PlaceSymbolicOperationMode(BaseControlComponent<DoorAdjustmentStationService> component) {
        super(component);
    }

    @Override
    public void onStarting() {
        super.onStarting();
        executing = true;
        getService(DoorAdjustmentStationService.class).placeSymbolic(workstepId);
    }

    @Override
    public void onCompleting() {
        super.onCompleting();
        DoorAdjustmentStationServiceImpl.currentOpMode = DoorAdjustmentStationStatus.OPMode.NONE;
        DoorAdjustmentStationServiceImpl.expected_workstep_id = "";
        DoorAdjustmentStationServiceImpl.current_workstep_id = "-1";
    }

    @Override
    public void onStopping() {
        super.onStopping();
        DoorAdjustmentStationServiceImpl.currentOpMode = DoorAdjustmentStationStatus.OPMode.NONE;
        DoorAdjustmentStationServiceImpl.expected_workstep_id = "";
        DoorAdjustmentStationServiceImpl.current_workstep_id = "-1";
    }
}
