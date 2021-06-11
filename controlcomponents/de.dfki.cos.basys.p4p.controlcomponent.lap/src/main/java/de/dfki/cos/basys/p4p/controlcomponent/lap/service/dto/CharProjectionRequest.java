package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class CharProjectionRequest {
	
	private String action;
	private CharProjectionData data;
	
	public CharProjectionRequest() {
		this.action = "projection";
	}
	
	public CharProjectionRequest(double x, double y, double z, int color, String chr, double height) {
		this.action = "projection";
		this.setData(new CharProjectionData(x, y, z, color, chr, height));
	}
	
	public String getAction() {
		return action;
	}

	public CharProjectionData getData() {
		return data;
	}

	public void setData(CharProjectionData data) {
		this.data = data;
	}
	
	

}
