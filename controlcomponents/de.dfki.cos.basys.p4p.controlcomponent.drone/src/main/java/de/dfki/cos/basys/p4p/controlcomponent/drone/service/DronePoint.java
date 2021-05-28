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

	public Vector3d getPos() {
		return pos;
	}

	public void setPos(Vector3d pos) {
		this.pos = pos;
	}

	public double getRot() {
		return rot;
	}

	public void setRot(double rot) {
		this.rot = rot;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
}
