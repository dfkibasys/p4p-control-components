package de.dfki.cos.basys.p4p.controlcomponent.vacuum.service;

import java.util.ArrayList;
import java.util.List;

import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.VacuumStatus.PState;


public class PhysicalState {	

	PState currentState;
	private final List<PhysicalStateListener> stateListeners = new ArrayList<>();
	
	private static final class InstanceHolder {
		static final PhysicalState INSTANCE = new PhysicalState();
	}
	
	private PhysicalState() {
		this.currentState = PState.ONGROUND;
	}
	
	public static PhysicalState getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public PState getState() {
		return this.currentState;
	}
	
	public void setState(PState newState) {
		PState oldState = this.currentState;
		this.currentState = newState;
		
		for (PhysicalStateListener ping : stateListeners) {
			ping.stateChangedEvent(oldState, newState);
		}
	}
	
	public void addStateListener(PhysicalStateListener toadd) {
		stateListeners.add(toadd);
	}
	
	public void removeStateListener(PhysicalStateListener toremove) {
		stateListeners.remove(toremove);
	}
	
	public void removeStateListeners() {
		stateListeners.clear();
	}

}
