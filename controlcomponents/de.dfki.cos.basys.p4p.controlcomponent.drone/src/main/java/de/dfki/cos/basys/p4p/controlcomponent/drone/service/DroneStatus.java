package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

public class DroneStatus {
	
	public enum PhysicalState {
		ONGROUND, TAKINGOFF, HOVERING, MOVING, LANDING
	}
	
	public enum MissionState {
		ACCEPTED, REJECTED, EXECUTING, DONE, PENDING, FAILED, ABORTED
	}
	
	public enum WorkState {
		PHASE0, PHASE1, PHASE2, PHASE_IDLE
	}
	
//	public enum LogicalState {
//		IDLE, EXECUTING, PAUSED, ABORTED
//	}
	
	PhysicalState physicalState;
	MissionState missionState;
	WorkState workState;
	
}
