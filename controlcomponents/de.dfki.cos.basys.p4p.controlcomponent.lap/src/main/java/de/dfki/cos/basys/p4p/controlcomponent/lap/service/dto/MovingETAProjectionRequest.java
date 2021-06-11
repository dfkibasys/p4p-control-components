package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class MovingETAProjectionRequest {
	
	private String action;
	private MovingETAProjectionData data;
	
	public MovingETAProjectionRequest() {
		this.action = "projection";
	}
	
	public MovingETAProjectionRequest(double x, double y, double z, int color, double radius, double angle, double fullTime, double startTime) {
		this.action = "projection";
		this.setData(new MovingETAProjectionData(x, y, z, color, radius, angle, fullTime, startTime));
	}
	
	public String getAction() {
		return action;
	}

	public MovingETAProjectionData getData() {
		return data;
	}

	public void setData(MovingETAProjectionData data) {
		this.data = data;
	}
	
}
