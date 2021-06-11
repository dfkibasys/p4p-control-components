package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

import java.util.List;

public class ArrowsAndCirclesProjectionData extends GeneralData {
	
	private List<Point> points;
	private int arrowCount;
	private double innerCircleRadius;
	private double middleCircleRadius;
	private double outerCircleRadius;
	private double angleStart;
	private double angleLength;
	private int delayArrows;
	private int delayCirclers;
	
	public ArrowsAndCirclesProjectionData() {
		this.type ="arrowsAndCircles";
	}
	
	public ArrowsAndCirclesProjectionData(double x, double y, double z, int color, List<Point> points, int arrowCount, double innerCircleRadius, double middleCircleRadius, double outerCircleRadius, double angleStart, double angleLength, int delayArrows, int delayCircles) {
		this.type ="arrowsAndCircles";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setPoints(points);
		this.setArrowCount(arrowCount);
		this.setInnerCircleRadius(innerCircleRadius);
		this.setMiddleCircleRadius(middleCircleRadius);
		this.setOuterCircleRadius(outerCircleRadius);
		this.setAngleStart(angleStart);
		this.setAngleLength(angleLength);
		this.setDelayArrows(delayArrows);
		this.setDelayCirclers(delayCircles);
	}
	
	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
	public int getArrowCount() {
		return arrowCount;
	}
	public void setArrowCount(int arrowCount) {
		this.arrowCount = arrowCount;
	}
	public double getInnerCircleRadius() {
		return innerCircleRadius;
	}
	public void setInnerCircleRadius(double innerCircleRadius) {
		this.innerCircleRadius = innerCircleRadius;
	}
	public double getMiddleCircleRadius() {
		return middleCircleRadius;
	}
	public void setMiddleCircleRadius(double middleCircleRadius) {
		this.middleCircleRadius = middleCircleRadius;
	}
	public double getOuterCircleRadius() {
		return outerCircleRadius;
	}
	public void setOuterCircleRadius(double outerCircleRadius) {
		this.outerCircleRadius = outerCircleRadius;
	}
	public double getAngleStart() {
		return angleStart;
	}
	public void setAngleStart(double angleStart) {
		this.angleStart = angleStart;
	}
	public double getAngleLength() {
		return angleLength;
	}
	public void setAngleLength(double angleLength) {
		this.angleLength = angleLength;
	}
	public int getDelayArrows() {
		return delayArrows;
	}
	public void setDelayArrows(int delayArrows) {
		this.delayArrows = delayArrows;
	}
	public int getDelayCirclers() {
		return delayCirclers;
	}
	public void setDelayCirclers(int delayCirclers) {
		this.delayCirclers = delayCirclers;
	}

}
