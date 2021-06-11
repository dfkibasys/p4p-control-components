package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

import java.util.List;

public class ArrowsAndCirclesProjectionRequest {
	private String action;
	private ArrowsAndCirclesProjectionData data;
	
	public ArrowsAndCirclesProjectionRequest() {
		this.action = "projection";
	}
	
	public ArrowsAndCirclesProjectionRequest(double x, double y, double z, int color, List<Point> points, int arrowCount, double innerCircleRadius, double middleCircleRadius, double outerCircleRadius, double angleStart, double angleLength, int delayArrows, int delayCircles){
		this.action = "projection";
		this.setData(new ArrowsAndCirclesProjectionData(x, y, z, color, points, arrowCount, innerCircleRadius, middleCircleRadius, outerCircleRadius, angleStart, angleLength, delayArrows, delayCircles));
	}
	
	public String getAction() {
		return action;
	}
	public ArrowsAndCirclesProjectionData getData() {
		return data;
	}
	public void setData(ArrowsAndCirclesProjectionData data) {
		this.data = data;
	}

}
