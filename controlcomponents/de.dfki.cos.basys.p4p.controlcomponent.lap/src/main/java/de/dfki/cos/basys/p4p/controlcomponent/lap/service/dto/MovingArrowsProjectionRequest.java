package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

import java.util.List;

public class MovingArrowsProjectionRequest {
	private String action;
	private MovingArrowsProjectionData data;
	
	public MovingArrowsProjectionRequest() {
		this.action = "projection";
	}
	
	public MovingArrowsProjectionRequest(double x, double y, double z, int color, List<Point> points, int arrowCount, int delay){
		this.action = "projection";
		this.setData(new MovingArrowsProjectionData(x, y, z, color, points, arrowCount, delay));
	}
	
	public String getAction() {
		return action;
	}
	public MovingArrowsProjectionData getData() {
		return data;
	}
	public void setData(MovingArrowsProjectionData data) {
		this.data = data;
	}

}
