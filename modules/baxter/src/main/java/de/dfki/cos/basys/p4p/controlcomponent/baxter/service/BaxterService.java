package de.dfki.cos.basys.p4p.controlcomponent.baxter.service;

import edu.wpi.rail.jrosbridge.Goal.GoalStatusEnum;

public interface BaxterService {
	
	boolean removeObstacle(String type, int x, int y);
	
	void gotoSymbolicPosition(String positionName);
	
	GoalStatusEnum getStatus();
	
	void reset();
	
}
