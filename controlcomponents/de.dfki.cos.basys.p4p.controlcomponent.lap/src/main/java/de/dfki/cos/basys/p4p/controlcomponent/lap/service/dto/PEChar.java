package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class PEChar extends ProjectionEntity {
	
	private char chr;
	private double height;
	
	public PEChar() {
		this.type = "char";
	}
	
	public PEChar(double x, double y, double z, int color, char chr, double height) {
		this.type = "char";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setChr(chr);
		this.setHeight(height);
	}
	
	public char getChr() {
		return chr;
	}
	public void setChr(char chr) {
		this.chr = chr;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	

}
