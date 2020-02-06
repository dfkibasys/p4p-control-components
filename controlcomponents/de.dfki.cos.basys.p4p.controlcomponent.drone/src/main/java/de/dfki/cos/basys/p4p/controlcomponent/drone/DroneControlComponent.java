package de.dfki.cos.basys.p4p.controlcomponent.drone;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

public class DroneControlComponent extends BaseControlComponent<DroneService> {

	public DroneControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		OperationMode opMode = new MoveToSymbolicPositionOperationMode(this);
		registerOperationMode(opMode);		
	}
	
}
