package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

import java.util.List;

public interface DroneService {
	void moveToSymbolicPosition(String position);
	void takeOff();
	void startVideoStream();
	void stopVideoStream();
	void land();
	String getMissionState();
	String getWorkState();
	String getStatus();
	List<String> getSymbolicPositions();
	void reset();
}