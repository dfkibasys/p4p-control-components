package de.dfki.cos.basys.p4p.controlcomponent.laplaser.service.dto;

public abstract class ProjectionEntity {
	
	protected String type;
	private double x;
	private double y;
	private double z;
	private int color;
	
	public String getType() {
		return type;
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
	
	public double getZ() {
		return z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}
