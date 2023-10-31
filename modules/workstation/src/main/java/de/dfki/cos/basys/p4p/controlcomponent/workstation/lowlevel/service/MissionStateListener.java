package de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service;

import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service.WorkstationStatus.MState;

public interface MissionStateListener {
	public void stateChangedEvent(MState oldState, MState newState);
}