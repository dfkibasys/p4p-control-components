package de.dfki.cos.basys.p4p.controlcomponent.laplaser.service.dto;

public class PEEllipse extends ProjectionEntity {
	
	private double majorRadius;
	private double minorRadius;
	private double angleStart;
	private double angleLength;
	
	public PEEllipse() {
		this.type = "ellipse";
	}
	
	public PEEllipse(double x, double y, double z, int color, double majorRadius, double minorRadius, double angleStart, double angleLength) {
		this.type = "ellipse";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setMajorRadius(majorRadius);
		this.setMinorRadius(minorRadius);
		this.setAngleStart(angleStart);
		this.setAngleLength(angleLength);
	}
	
	public double getMajorRadius() {
		return majorRadius;
	}

	public void setMajorRadius(double majorRadius) {
		this.majorRadius = majorRadius;
	}

	public double getMinorRadius() {
		return minorRadius;
	}

	public void setMinorRadius(double minorRadius) {
		this.minorRadius = minorRadius;
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
