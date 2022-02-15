package de.dfki.cos.basys.p4p.controlcomponent.vacuum.service;

import java.util.ArrayList;
import java.util.List;

import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.VacuumStatus.WState;


public class WorkState {	

	WState currentState;
	private final List<WorkStateListener> stateListeners = new ArrayList<>();
	
	private static final class InstanceHolder {
		static final WorkState INSTANCE = new WorkState();
	}
	
	private WorkState() {
		this.currentState = WState.IDLE;
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
		
		for (WorkStateListener ping : stateListeners) {
			ping.stateChangedEvent(oldState, newState);
		}
	}
	
	public void addStateListener(WorkStateListener toadd) {
		stateListeners.add(toadd);
	}
	
	public void removeStateListener(WorkStateListener toremove) {
		stateListeners.remove(toremove);
	}
	
	public void removeStateListeners() {
		stateListeners.clear();
	}

}
