package de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service;

import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.SmartwatchStatus.TState;

public interface TaskStateListener {
	public void stateChangedEvent(TState oldState, TState newState);
}