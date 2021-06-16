package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class PEPulsatingCircle extends ProjectionEntity {
	
	private double innerCircleRadius;
	private double middleCircleRadius;
	private double outerCircleRadius;
	private double angleStart;
	private double angleLength;
	private int delay;
	
	public PEPulsatingCircle() {
		this.type ="pulsatingCircle";
	}
	
	public PEPulsatingCircle(double x, double y, double z, int color, double innerCircleRadius, double middleCircleRadius, double outerCircleRadius,  double angleStart, double angleLength, int delay) {
		this.type ="pulsatingCircle";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setInnerCircleRadius(innerCircleRadius);
		this.setMiddleCircleRadius(middleCircleRadius);
		this.setOuterCircleRadius(outerCircleRadius);
		this.setAngleStart(angleStart);
		this.setAngleLength(angleLength);
		this.setDelay(delay);
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

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}


}
