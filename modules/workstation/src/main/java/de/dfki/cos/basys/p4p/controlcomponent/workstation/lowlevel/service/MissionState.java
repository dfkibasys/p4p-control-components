package de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service;


import java.util.ArrayList;
import java.util.List;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service.WorkstationStatus.MState;


public class MissionState {

	MState currentState;
	private final List<MissionStateListener> stateListeners = new ArrayList<>();

	private static final class InstanceHolder {
		static final MissionState INSTANCE = new MissionState();
	}

	private MissionState() {
		this.currentState = WorkstationStatus.MState.PENDING;
	}

	public static MissionState getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public MState getState() {
		return this.currentState;
	}

	public void setState(MState newState) {
		MState oldState = this.currentState;
		this.currentState = newState;

		for (MissionStateListener ping : stateListeners) {
			ping.stateChangedEvent(oldState, newState);
		}
	}

	public void addStateListener(MissionStateListener toadd) {
		stateListeners.add(toadd);
	}

	public void removeStateListener(MissionStateListener toremove) {
		stateListeners.remove(toremove);
	}

	public void removeStateListeners() {
		stateListeners.clear();
	}

}
