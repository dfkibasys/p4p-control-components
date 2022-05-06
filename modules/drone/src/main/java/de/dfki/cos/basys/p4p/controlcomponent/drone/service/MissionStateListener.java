package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MState;

public interface MissionStateListener {
	public void stateChangedEvent(MState oldState, MState newState);
}