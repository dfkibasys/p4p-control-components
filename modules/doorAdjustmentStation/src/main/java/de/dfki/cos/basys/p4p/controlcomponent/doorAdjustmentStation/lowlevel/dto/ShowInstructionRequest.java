package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.dto;

import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationStatus.TASK;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ShowInstructionRequest {
    public String instructionMsg;
    public String instructionImage;
    public TASK taskId;
    public String icon;
    public String highlightRef;
}
