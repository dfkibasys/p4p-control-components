package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class StringProjectionRequest {
	
	private String action;
	private StringProjectionData data;
	
	public StringProjectionRequest() {
		this.action = "projection";
	}
	
	public StringProjectionRequest(double x, double y, double z, int color, String text, double height) {
		this.action = "projection";
		this.setData(new StringProjectionData(x, y, z, color, text, height));
	}
	
	public String getAction() {
		return action;
	}

	public StringProjectionData getData() {
		return data;
	}

	public void setData(StringProjectionData data) {
		this.data = data;
	}
	
	

}
