package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.dto;

import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationStatus.TASK;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class InstructionResponse {
    private TASK taskId;
    private Boolean success;
    private String message;
}
