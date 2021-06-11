package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class MovingETAProjectionData extends GeneralData {
	
	private double radius;
	private double angle;
	private double fullTime;
	private double startTime;
	
	public MovingETAProjectionData() {
		this.type = "movingETA";
	}
	
	public MovingETAProjectionData(double x, double y, double z, int color, double radius, double angle, double fullTime, double startTime) {
		this.type = "movingETA";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setRadius(radius);
		this.setAngle(angle);
		this.setFullTime(fullTime);
		this.setStartTime(startTime);

	}
	
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public double getFullTime() {
		return fullTime;
	}

	public void setFullTime(double fullTime) {
		this.fullTime = fullTime;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
	
	



}
