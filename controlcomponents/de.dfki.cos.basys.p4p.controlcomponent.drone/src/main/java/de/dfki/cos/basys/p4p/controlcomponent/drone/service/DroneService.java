package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

import java.util.List;

public interface DroneService {
	void moveToSymbolicPosition(String position);
	void takeOff();
	void startLiveImage();
	void stopLiveImage();
	void startRTMPStream();
	void stopRTMPStream();
	void land();
	String getMissionState();
	String getWorkState();
	String getStatus();
	List<String> getSymbolicPositions();
	List<String> detectObstacles(String type);
	void reset();
	void abort();
	void pause();
	void resume();
}