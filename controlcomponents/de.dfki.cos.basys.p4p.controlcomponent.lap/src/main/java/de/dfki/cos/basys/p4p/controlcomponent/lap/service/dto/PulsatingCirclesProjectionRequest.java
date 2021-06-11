package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class PulsatingCirclesProjectionRequest {
	private String action;
	private PulsatingCircleProjectionData data;
	
	public PulsatingCirclesProjectionRequest() {
		this.action = "projection";
	}
	
	public PulsatingCirclesProjectionRequest(double x, double y, double z, int color, double innerCircleRadius, double middleCircleRadius, double outerCircleRadius,  double angleStart, double angleLength, int delay){
		this.action = "projection";
		this.setData(new PulsatingCircleProjectionData(x, y, z, color, innerCircleRadius, middleCircleRadius, outerCircleRadius, angleStart, angleLength, delay));
	}
	
	public String getAction() {
		return action;
	}
	public PulsatingCircleProjectionData getData() {
		return data;
	}
	public void setData(PulsatingCircleProjectionData data) {
		this.data = data;
	}

}
