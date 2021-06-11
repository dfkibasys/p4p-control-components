package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class CircleProjectionData extends GeneralData {
	
	private double radius;
	private double angleStart;
	private double angleLength;
	
	public CircleProjectionData() {
		this.type = "circle";
	}
	
	public CircleProjectionData(double x, double y, double z, int color, double radius, double angleStart, double angleLength) {
		this.type = "circle";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setRadius(radius);
		this.setAngleStart(angleStart);
		this.setAngleLength(angleLength);
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
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
	


}
