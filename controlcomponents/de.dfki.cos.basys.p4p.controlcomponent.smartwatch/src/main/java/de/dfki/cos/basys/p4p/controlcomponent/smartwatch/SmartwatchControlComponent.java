package de.dfki.cos.basys.p4p.controlcomponent.smartwatch;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

public class SmartwatchControlComponent extends BaseControlComponent<NotificationService> {

	public SmartwatchControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		OperationMode opMode = new DisplayInfoMessageOperationMode(this);
		registerOperationMode(opMode);	
		
		OperationMode opMode2 = new RequestTaskExecutionOperationMode(this);
		registerOperationMode(opMode2);	
	}
	
}
