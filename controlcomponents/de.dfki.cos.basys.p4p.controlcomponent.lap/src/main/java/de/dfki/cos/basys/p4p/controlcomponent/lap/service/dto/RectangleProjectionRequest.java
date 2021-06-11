package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class RectangleProjectionRequest {
	private String action;
	private RectangleProjectionData data;
	
	public RectangleProjectionRequest(){
		this.action = "projection";
	}
	
	public RectangleProjectionRequest(double x, double y, double z, int color, double width, double height){
		this.action = "projection";
		this.setData(new RectangleProjectionData(x, y, z, color, width, height));
	}
	
	public String getAction() {
		return action;
	}
	
	public RectangleProjectionData getData() {
		return data;
	}
	
	public void setData(RectangleProjectionData data) {
		this.data = data;
	}
}
