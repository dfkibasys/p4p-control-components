package de.dfki.cos.basys.p4p.controlcomponent.ur.service;

import de.dfki.cos.basys.p4p.controlcomponent.ur.service.URState.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.URState.WorkState;


public interface UrService {
	
	void moveToSymbolicPosition(String positionName);
	void pickAndPlaceSymbolic(String objectType, String sourceLocation, String targetLocation);
	void pickSymbolic(String objectType, String sourceLocation);
	void placeSymbolic(String objectType, String targetLocation);
	void joinSymbolic(String objectTypeA, String objectTypeB);
	
	MissionState getMissionState();
	WorkState getWorkState();
	String getStatus();
	
	void reset();
	
}
