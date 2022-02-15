package de.dfki.cos.basys.p4p.controlcomponent.vacuum.service;

import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.VacuumStatus.MState;

public interface MissionStateListener {
	public void stateChangedEvent(MState oldState, MState newState);
}