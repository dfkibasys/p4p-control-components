package de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service;

public interface WorkstationService {
    void pickSymbolic(String material, String mat_location, int quantity);
    void placeSymbolic(String workstepId);
    void joinSymbolic(String material_a, String material_b, String tool);
    void reset();
    MissionState getMissionState();
}