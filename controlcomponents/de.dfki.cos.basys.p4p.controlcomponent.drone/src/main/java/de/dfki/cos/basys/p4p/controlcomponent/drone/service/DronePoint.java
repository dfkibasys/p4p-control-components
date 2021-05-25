package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

public class DronePoint {

	private Vector3d pos;
	private double rot;
	private double pitch;

	public DronePoint(double x, double y, double z, double rot, double pitch) {
		this.setPosition(new Vector3d(x, y, z));
		this.setRotation(rot);
		this.setPitch(pitch);
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
