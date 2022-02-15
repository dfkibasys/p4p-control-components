package de.dfki.cos.basys.p4p.controlcomponent.vacuum.service;

import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.VacuumStatus.WState;

public interface WorkStateListener {
	public void stateChangedEvent(WState oldState, WState newState);
}