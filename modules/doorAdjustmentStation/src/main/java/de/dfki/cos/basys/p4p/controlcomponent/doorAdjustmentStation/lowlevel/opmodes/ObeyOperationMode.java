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

@OperationMode(name = "Obey", shortName = "OBEY", description = "Obey the instruction",
        allowedCommands = {ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP},
        allowedModes = { ExecutionMode.AUTO, ExecutionMode.SIMULATE })
public class ObeyOperationMode extends BaseDoorAdjustmentStationOperationMode {

    @Parameter(name = "sao_workstep_id", direction = ParameterDirection.IN)
    private String workstepId = "";

    public ObeyOperationMode(BaseControlComponent<DoorAdjustmentStationService> component) {
        super(component);
    }

    @Override
    public void onStarting() {
        super.onStarting();
        executing = true;
        getService(DoorAdjustmentStationService.class).obey(workstepId);
    }

    @Override
    public void onCompleting() {
        super.onCompleting();
        // reset variables
        DoorAdjustmentStationServiceImpl.currentOpMode = DoorAdjustmentStationStatus.OPMode.NONE;
    }

    @Override
    public void onStopping() {
        super.onStopping();
        // reset variables
        DoorAdjustmentStationServiceImpl.currentOpMode = DoorAdjustmentStationStatus.OPMode.NONE;
    }
}
