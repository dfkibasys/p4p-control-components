package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

public class DronePoint {

	private Vector3d pos;
	private double rot;
	private double pitch;
	
	public DronePoint() {
		super();
	}

	public DronePoint(double x, double y, double z, double rot, double pitch) {
		this.pos = new Vector3d(x, y, z);
		this.rot = rot;
		this.pitch = pitch;
	}

	public Vector3d getPosition() {
		return pos;
	}

	public void setPosition(Vector3d pos) {
		this.pos = pos;
	}

	public double getRotation() {
		return rot;
	}

	public void setRotation(double rot2) {
		this.rot = rot2;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
}
