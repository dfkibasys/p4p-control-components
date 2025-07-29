package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service;

public interface DoorAdjustmentStationService {
    void obey(String workstepId);
    void reset();
    MissionState getMissionState();
}