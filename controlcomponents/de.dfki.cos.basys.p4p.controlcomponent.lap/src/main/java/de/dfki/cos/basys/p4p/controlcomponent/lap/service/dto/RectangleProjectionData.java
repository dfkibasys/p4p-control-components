package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class RectangleProjectionData extends GeneralData {

	private double width;
	private double height;
	
	public RectangleProjectionData() {
		this.type = "rectangle";
	}
	
	public RectangleProjectionData(double x, double y, double z, int color, double width, double height) {
		this.type = "rectangle";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public double getWidth() {
		return width;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	
	public double getHeight() {
		return height;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}


}
