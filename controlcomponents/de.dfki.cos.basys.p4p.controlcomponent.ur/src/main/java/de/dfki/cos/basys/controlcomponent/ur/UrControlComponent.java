package de.dfki.cos.basys.controlcomponent.ur;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

public class UrControlComponent extends BaseControlComponent {

	public UrControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		OperationMode opMode = new ExampleOperationMode(this);
		registerOperationMode(opMode);		
	}
	
}
