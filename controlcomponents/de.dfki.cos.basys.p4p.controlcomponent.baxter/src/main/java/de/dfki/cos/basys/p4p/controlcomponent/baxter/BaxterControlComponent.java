package de.dfki.cos.basys.p4p.controlcomponent.baxter;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

public class BaxterControlComponent extends BaseControlComponent<BaxterService> {

	public BaxterControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		registerOperationMode(new MoveToSymbolicPositionOperationMode(this));
		registerOperationMode(new RemoveObstacleOperationMode(this));		
	}
	
}
