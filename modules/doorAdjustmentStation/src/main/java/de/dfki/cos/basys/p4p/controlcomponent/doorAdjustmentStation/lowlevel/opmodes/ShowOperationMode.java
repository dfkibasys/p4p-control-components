package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.opmodes;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationService;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationServiceImpl;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationStatus.OPMode;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationStatus.TASK;

@OperationMode(name = "Show", shortName = "SHOW", description = "Show the instruction",
        allowedCommands = {ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP},
        allowedModes = { ExecutionMode.AUTO, ExecutionMode.SIMULATE })
public class ShowOperationMode extends BaseDoorAdjustmentStationOperationMode {

    @Parameter(name = "show_task_id", direction = ParameterDirection.IN)
    private String taskId = "";

    public ShowOperationMode(BaseControlComponent<DoorAdjustmentStationService> component) {
        super(component);
    }

    @Override
    public void onStarting() {
        super.onStarting();
        executing = true;
        getService(DoorAdjustmentStationService.class).show(taskId);
    }

    @Override
    public void onCompleting() {
        super.onCompleting();
        // reset variables
        DoorAdjustmentStationServiceImpl.currentOpMode = OPMode.NONE;
        DoorAdjustmentStationServiceImpl.currentTask = TASK.NONE;
    }

    @Override
    public void onStopping() {
        super.onStopping();
        // reset variables
        DoorAdjustmentStationServiceImpl.currentOpMode = OPMode.NONE;
        DoorAdjustmentStationServiceImpl.currentTask = TASK.NONE;
    }
}
