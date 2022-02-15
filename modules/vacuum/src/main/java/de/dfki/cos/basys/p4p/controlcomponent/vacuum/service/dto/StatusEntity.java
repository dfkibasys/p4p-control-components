package de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.dto;

public class StatusEntity {
	public String error;
	public int error_code;
	public int battery;
	public int fanspeed;
	public boolean in_zone_cleaning;
	public boolean in_segment_cleaning;
	public boolean is_paused;
	public boolean is_on;
	public boolean is_water_box_attached;
	public String map;
	public String state;
	public int state_code;
	public float clean_area;
	public String clean_time;
}
