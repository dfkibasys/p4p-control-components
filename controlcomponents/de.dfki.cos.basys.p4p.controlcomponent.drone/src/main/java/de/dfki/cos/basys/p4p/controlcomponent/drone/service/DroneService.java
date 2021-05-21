package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

import java.util.List;

import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.WorkState;

public interface DroneService {
	void moveToSymbolicPosition(String position);
	void moveToPoint(DronePoint point);
	void moveToWaypoints(List<DronePoint> waypoints);
	void takeOff();
	void startLiveImage();
	void stopLiveImage();
	void startRTMPStream();
	void stopRTMPStream();
	void land();
	MissionState getMissionState();
	WorkState getWorkState();
	String getStatus();
	List<String> getSymbolicPositions();
	List<String> detectObstacles(String type);
	void reset();
	void abort();
	void pause();
	void resume();
}