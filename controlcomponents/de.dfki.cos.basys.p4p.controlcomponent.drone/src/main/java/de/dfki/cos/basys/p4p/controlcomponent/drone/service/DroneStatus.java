package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

public class DroneStatus {
	
	public enum PhysicalState {
		ONGROUND, TAKINGOFF, HOVERING, MOVING, LANDING
	}
	
//	public enum LogicalState {
//		IDLE, EXECUTING, PAUSED, ABORTED
//	}
	
	PhysicalState physicalState;
	
}
