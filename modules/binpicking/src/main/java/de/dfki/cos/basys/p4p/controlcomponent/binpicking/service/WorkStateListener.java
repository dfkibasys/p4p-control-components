package de.dfki.cos.basys.p4p.controlcomponent.binpicking.service;

import de.dfki.cos.basys.p4p.controlcomponent.binpicking.service.BinPickingStatus.WState;

public interface WorkStateListener {
	public void stateChangedEvent(WState oldState, WState newState);
}