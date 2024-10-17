package de.dfki.cos.basys.p4p.controlcomponent.binpicking.service;

import de.dfki.cos.basys.p4p.controlcomponent.binpicking.service.BinPickingStatus.MState;

public interface MissionStateListener {
	public void stateChangedEvent(MState oldState, MState newState);
}