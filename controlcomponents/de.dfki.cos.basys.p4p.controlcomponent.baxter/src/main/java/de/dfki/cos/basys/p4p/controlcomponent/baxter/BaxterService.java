package de.dfki.cos.basys.p4p.controlcomponent.baxter;

public interface BaxterService {

	boolean removeObstacle(String type, int x, int y);
	
	String getStatus();
	
}
