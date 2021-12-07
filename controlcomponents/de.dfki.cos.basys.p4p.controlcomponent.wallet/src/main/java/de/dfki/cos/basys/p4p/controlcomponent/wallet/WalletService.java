package de.dfki.cos.basys.p4p.controlcomponent.wallet;

import edu.wpi.rail.jrosbridge.Goal.GoalStatusEnum;

public interface WalletService {
		
	void gotoSymbolicPosition(String positionName);
	void moveLiftToHeight(double height);
	
	GoalStatusEnum getLiftStatus();
	GoalStatusEnum getGotoStatus();
	
}
