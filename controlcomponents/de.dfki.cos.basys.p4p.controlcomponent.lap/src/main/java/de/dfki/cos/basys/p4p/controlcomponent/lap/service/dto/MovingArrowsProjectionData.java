package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

import java.util.List;

public class MovingArrowsProjectionData extends GeneralData {
	
	private List<Point> points;
	private int arrowCount;
	private int delay;
	
	public MovingArrowsProjectionData() {
		this.type ="movingArrows";
	}
	
	public MovingArrowsProjectionData(double x, double y, double z, int color, List<Point> points, int arrowCount, int delay) {
		this.type ="movingArrows";
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setColor(color);
		this.setPoints(points);
		this.setArrowCount(arrowCount);
		this.setDelay(delay);
	}
	
	public List<Point> getPoints() {
		return points;
	}
	
	public void setPoints(List<Point> points) {
		this.points = points;
	}
	
	public int getArrowCount() {
		return arrowCount;
	}
	
	public void setArrowCount(int arrowCount) {
		this.arrowCount = arrowCount;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

}
