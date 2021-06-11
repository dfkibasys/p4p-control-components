package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class CircleProjectionRequest {
	
	private String action;
	private CircleProjectionData data;
	
	public CircleProjectionRequest() {
		this.action = "projection";
	}
	
	public CircleProjectionRequest(double x, double y, double z, int color, double radius, double angleStart, double angleLength) {
		this.action = "projection";
		this.setData(new CircleProjectionData(x, y, z, color, radius, angleStart, angleLength));
	}
	
	public String getAction() {
		return action;
	}

	public CircleProjectionData getData() {
		return data;
	}

	public void setData(CircleProjectionData data) {
		this.data = data;
	}
	
	

}
