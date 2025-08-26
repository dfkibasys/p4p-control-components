package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.dto;

import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service.DoorAdjustmentStationStatus.TASK;

public class InstructionResponse {
    public TASK taskId;
    public Boolean success;
    public String message;

    @Override
    public String toString() {
        return "Instruction{" +
                "taskId='" + taskId + '\'' +
                ", success=" + success +
                ", message=" + message +
                '}';
    }
}
