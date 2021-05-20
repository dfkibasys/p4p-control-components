package de.dfki.cos.basys.p4p.controlcomponent.ur.service;

import edu.wpi.rail.jrosbridge.Goal.GoalStatusEnum;
import edu.wpi.rail.jrosbridge.messages.actionlib.GoalID;

public interface URService {
	
	void moveToSymbolicPosition(String positionName);
	
	GoalStatusEnum getGoalStatus(GoalID goal);
	GoalStatusEnum getStatus();
	
	void reset();
	
}
