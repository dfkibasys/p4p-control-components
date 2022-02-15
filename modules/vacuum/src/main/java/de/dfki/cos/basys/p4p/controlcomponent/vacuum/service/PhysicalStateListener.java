package de.dfki.cos.basys.p4p.controlcomponent.vacuum.service;

import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.VacuumStatus.PState;

public interface PhysicalStateListener {
	public void stateChangedEvent(PState oldState, PState newState);
}