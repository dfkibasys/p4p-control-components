package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class StringProjectionData extends GeneralData {
	
	private String text;
	private double height;
	
	public StringProjectionData() {
		this.type = "string";
	}
	
	public StringProjectionData(double x, double y, double z, int color, String text, double height) {
		this.type = "string";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setText(text);
		this.setHeight(height);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getHeight() {
		return height;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}


}
