package de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service;

public class WorkstationStatus {

	public enum MState {
		ACCEPTED, REJECTED, EXECUTING, DONE, PENDING, FAILED, ABORTED
	}

}
