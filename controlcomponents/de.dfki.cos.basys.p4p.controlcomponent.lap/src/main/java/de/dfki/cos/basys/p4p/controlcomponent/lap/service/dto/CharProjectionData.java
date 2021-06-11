package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class CharProjectionData extends GeneralData {
	
	private String chr;
	private double height;
	
	public CharProjectionData() {
		this.type = "char";
	}
	
	public CharProjectionData(double x, double y, double z, int color, String chr, double height) {
		this.type = "char";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setChr(chr);
		this.setHeight(height);
	}
	
	public String getChr() {
		return chr;
	}
	public void setChr(String chr) {
		this.chr = chr;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	

}
