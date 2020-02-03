package de.dfki.cos.basys.p4p.controlcomponent.baxter;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

public class BaxterRobotControlComponent extends BaseControlComponent {

	public BaxterRobotControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		OperationMode opMode = new ExampleOperationMode(this);
		registerOperationMode(opMode);		
	}
	
}
