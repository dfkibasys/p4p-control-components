package de.dfki.cos.basys.p4p.controlcomponent.lap.service;

public interface LapService {
	void projectRectangle(double x, double y, double z, double width, double height, int color);
	String getMissionState();
	String getWorkState();
	String getStatus();
	void reset();
	void abort();
	void pause();
	void resume();
}
