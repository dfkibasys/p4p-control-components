package de.dfki.cos.basys.p4p.controlcomponent.smartwatch;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

public class SmartwatchControlComponent extends BaseControlComponent {

	public SmartwatchControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		OperationMode opMode = new ExampleOperationMode(this);
		registerOperationMode(opMode);		
	}
	
}
