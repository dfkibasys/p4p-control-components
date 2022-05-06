package de.dfki.cos.basys.p4p.controlcomponent.laplaser.service.dto;

import java.util.ArrayList;
import java.util.List;

public class Projection {
	private String action;
	
	public List<ProjectionEntity> data = new ArrayList<>();
	
	public Projection() {
		this.action = "projection";
	}
	
	public void addEntity(ProjectionEntity entity) {
		data.add(entity);
	}

	public String getAction() {
		return action;
	}

}
