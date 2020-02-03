package de.dfki.cos.basys.p4p.controlcomponent.baxter;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

public class BaxterControlComponent extends BaseControlComponent {

	public BaxterControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		OperationMode opMode = new ExampleOperationMode(this);
		registerOperationMode(opMode);		
	}
	
}
