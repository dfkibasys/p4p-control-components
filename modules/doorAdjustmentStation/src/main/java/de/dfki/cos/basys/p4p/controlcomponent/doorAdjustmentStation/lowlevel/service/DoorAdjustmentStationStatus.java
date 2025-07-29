package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service;

public class DoorAdjustmentStationStatus {

	public enum MState {
		ACCEPTED, REJECTED, EXECUTING, DONE, PENDING, FAILED, ABORTED
	}

	public enum OPMode {
		NONE, OBEY
	}

}
