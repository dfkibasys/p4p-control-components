package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.WState;

public interface WorkStateListener {
	public void stateChangedEvent(WState oldState, WState newState);
}