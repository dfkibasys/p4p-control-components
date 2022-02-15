package de.dfki.cos.basys.p4p.controlcomponent.vacuum.service;


public interface VacuumService {
	void moveToPoint(Vector2d point);
	MissionState getMissionState();
	WorkState getWorkState();
	String getStatus();
	void reset();
	void abort();
	void pause();
	void resume();
}