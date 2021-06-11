package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class EllipseProjectionRequest {
	
	private String action;
	private EllipseProjectionData data;
	
	public EllipseProjectionRequest() {
		this.action = "projection";
	}
	
	public EllipseProjectionRequest(double x, double y, double z, int color, double majorRadius, double minorRadius, double angleStart, double angleLength) {
		this.action = "projection";
		this.setData(new EllipseProjectionData(x, y, z, color, majorRadius, minorRadius, angleStart, angleLength));
	}
	
	public String getAction() {
		return action;
	}

	public EllipseProjectionData getData() {
		return data;
	}

	public void setData(EllipseProjectionData data) {
		this.data = data;
	}
	
}
