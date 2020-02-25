package de.dfki.cos.basys.p4p.controlcomponent.drone;

import java.util.List;

public interface DroneService {
	void moveToSymbolicPosition(String position);
	void takeOff();
	void startVideoStream();
	void stopVideoStream();
	void land();
	String getMissionState();
	String getStatus();
	List<String> getSymbolicPositions();
	void reset();
}