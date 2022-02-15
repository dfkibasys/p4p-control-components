package de.dfki.cos.basys.p4p.controlcomponent.vacuum.service;

public class Vector2d {

	private double x;
	private double y;

	public Vector2d() {
		super();
	}
	
	public Vector2d(double x, double y) {
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
