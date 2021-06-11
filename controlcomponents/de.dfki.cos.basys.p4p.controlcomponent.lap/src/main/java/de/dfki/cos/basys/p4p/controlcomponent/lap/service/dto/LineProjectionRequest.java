package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class LineProjectionRequest {
	
	private String action;
	private LineProjectionData data;
	
	public LineProjectionRequest() {
		this.action = "projection";
	}
	
	public LineProjectionRequest(double x, double y, double z, int color, double x2, double y2, double z2) {
		this.action = "projection";
		this.setData(new LineProjectionData(x, y, z, color, x2, y2, z2));
	}
	
	public String getAction() {
		return action;
	}

	public LineProjectionData getData() {
		return data;
	}

	public void setData(LineProjectionData data) {
		this.data = data;
	}
	
}
