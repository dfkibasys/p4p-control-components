package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service;

public interface DoorAdjustmentStationService {
    void obey(String taskId);
    void show(String taskId);
    void reset();
    MissionState getMissionState();
}