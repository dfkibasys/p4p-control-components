package de.dfki.cos.basys.p4p.controlcomponent.ur.service;

public class URState {
	
	public enum MissionState {
		ACCEPTED, REJECTED, EXECUTING, DONE, PENDING, FAILED, CANCELLED
	}
	
	public enum WorkState {
		BUSY, FINISHED
	}
	
}
