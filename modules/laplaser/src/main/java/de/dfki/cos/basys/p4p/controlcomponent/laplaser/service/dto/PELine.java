package de.dfki.cos.basys.p4p.controlcomponent.laplaser.service.dto;

public class PELine extends ProjectionEntity {
	
	private double x2;
	private double y2;
	private double z2;
	
	public PELine() {
		this.type = "line";
	}
	
	public PELine(double x, double y, double z, int color, double x2, double y2, double z2) {
		this.type = "line";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setX2(x2);
		this.setY2(y2);
		this.setZ2(z2);
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
	}

	public double getZ2() {
		return z2;
	}

	public void setZ2(double z2) {
		this.z2 = z2;
	}
	

}
