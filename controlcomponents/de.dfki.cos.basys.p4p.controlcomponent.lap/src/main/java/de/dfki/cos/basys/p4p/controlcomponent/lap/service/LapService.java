package de.dfki.cos.basys.p4p.controlcomponent.lap.service;

import java.util.List;

import de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto.Point;

public interface LapService {
	void projectString(double x, double y, double z, int color, String text, double height);
	void projectChar(double x, double y, double z, int color, String chr, double height);
	void projectCircle(double x, double y, double z, int color, double radius, double angleStart, double angleLength);
	void projectEllipse(double x, double y, double z, int color, double majorRadius, double minorRadius, double angleStart, double angleLength);
	void projectLine(double x, double y, double z, int color, double x2, double y2, double z2);
	void projectRectangle(double x, double y, double z, int color, double width, double height);
	void projectMovingArrows(double x, double y, double z, int color, List<Point> points, int arrowCount, int delay);
	void projectMovingETA(double x, double y, double z, int color, double radius, double angle, double fullTime, double startTime);
	void projectPulsatingCircle(double x, double y, double z, int color, double innerCircleRadius, double middleCircleRadius, double outerCircleRadius,  double angleStart, double angleLength, int delay);
	void projectArrowsAndCircles(double x, double y, double z, int color, List<Point> points, int arrowCount, double innerCircleRadius, double middleCircleRadius, double outerCircleRadius, double angleStart, double angleLength, int delayArrows, int delayCircles);
	void stopProjection();
	String getMissionState();
	String getWorkState();
	String getStatus();
	void reset();
	void abort();
	void pause();
	void resume();
}
