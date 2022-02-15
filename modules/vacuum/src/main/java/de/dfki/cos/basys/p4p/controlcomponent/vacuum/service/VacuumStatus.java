package de.dfki.cos.basys.p4p.controlcomponent.vacuum.service;

public class VacuumStatus {
	
	public enum PState {
		ONGROUND, TAKINGOFF, HOVERING, MOVING, LANDING
	}
	
	public enum MState {
		ACCEPTED, REJECTED, EXECUTING, DONE, PENDING, FAILED, ABORTED
	}
	
	public enum WState {
		ERROR, DOCKED, IDLE, RETURNING, CLEANING, PAUSED, MANUAL_CONTROL, MOVING, CHARGING, ZONE_CLEANING
	}
	
}
