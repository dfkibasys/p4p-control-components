package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

public interface StateListener {
	public void stateChangedEvent(WState oldState, WState newState);
}