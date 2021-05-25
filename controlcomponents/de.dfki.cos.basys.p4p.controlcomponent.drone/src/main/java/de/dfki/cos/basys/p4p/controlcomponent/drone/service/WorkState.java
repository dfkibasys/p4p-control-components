package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

import java.util.ArrayList;
import java.util.List;

enum WState {
	PHASE0, PHASE1, PHASE2, PHASE_IDLE
}

public class WorkState {	

	WState currentState;
	private final List<StateListener> stateListeners = new ArrayList<>();
	
	private static final class InstanceHolder {
		static final WorkState INSTANCE = new WorkState();
	}
	
	private WorkState() {
		this.currentState = WState.PHASE_IDLE;
	}
	
	public static WorkState getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public WState getState() {
		return this.currentState;
	}
	
	public void setState(WState newState) {
		WState oldState = this.currentState;
		this.currentState = newState;
		
		for (StateListener ping : stateListeners) {
			ping.stateChangedEvent(oldState, newState);
		}
	}
	
	public void addStateListener(StateListener toadd) {
		stateListeners.add(toadd);
	}
	
	public void removeStateListener(StateListener toremove) {
		stateListeners.remove(toremove);
	}

}
