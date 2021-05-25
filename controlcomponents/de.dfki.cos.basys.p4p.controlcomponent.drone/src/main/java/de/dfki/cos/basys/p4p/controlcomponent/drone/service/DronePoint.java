package de.dfki.cos.basys.p4p.controlcomponent.drone.service;

public class DronePoint {

	private Vector3d pos;
	private float rot;
	private float pitch;

	public DronePoint(float x, float y, float z, float rot, float pitch) {
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

	public float getRotation() {
		return rot;
	}

	public void setRotation(float rot) {
		this.rot = rot;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}
