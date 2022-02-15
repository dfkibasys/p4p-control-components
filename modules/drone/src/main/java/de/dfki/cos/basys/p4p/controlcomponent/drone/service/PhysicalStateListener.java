package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.PState;

public interface PhysicalStateListener {
	public void stateChangedEvent(PState oldState, PState newState);
}