package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

public class DroneStatus {
	
	public enum PState {
		ONGROUND, TAKINGOFF, HOVERING, MOVING, LANDING
	}
	
	public enum MState {
		ACCEPTED, REJECTED, EXECUTING, DONE, PENDING, FAILED, ABORTED
	}
	
	public enum WState {
		PHASE0, PHASE1, PHASE2, PHASE_IDLE
	}
	
}
