package de.dfki.cos.basys.p4p.controlcomponent.mir.service;

import de.dfki.cos.basys.common.rest.mir.dto.MissionInstanceInfo;

public interface MissionHandler {
    void handleMission(MissionInstanceInfo mii);
    boolean isBusy();
}
