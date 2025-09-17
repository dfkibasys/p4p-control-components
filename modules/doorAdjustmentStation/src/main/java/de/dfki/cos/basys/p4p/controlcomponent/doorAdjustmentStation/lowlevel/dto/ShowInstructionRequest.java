package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.dto;

import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationStatus.TASK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowInstructionRequest {
    private String instructionMsg;
    private String instructionImage;
    private TASK taskId;
    private String icon;
    private String highlightRef;
}
