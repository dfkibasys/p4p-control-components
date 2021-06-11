package de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto;

public class StopProjectionRequest {
	private String action;
	
	public StopProjectionRequest() {
		this.action = "stop";
	}

	public String getAction() {
		return action;
	}
}
