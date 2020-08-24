package de.dfki.cos.basys.p4p.controlcomponent.wallet;

import edu.wpi.rail.jrosbridge.Goal.GoalStatusEnum;

public interface WalletService {
	
	//boolean removeObstacle(String type, int x, int y);
	
	void gotoSymbolicPosition(String positionName);
	
	void moveLiftToLevel(int level);
	
	GoalStatusEnum getStatus();
	
}
