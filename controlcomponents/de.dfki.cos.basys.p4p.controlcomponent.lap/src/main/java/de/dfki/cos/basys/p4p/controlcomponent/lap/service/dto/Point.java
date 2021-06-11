package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class Point {
	
	private double x;
	private double y;
	
	public Point() {
		super();
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}

}
