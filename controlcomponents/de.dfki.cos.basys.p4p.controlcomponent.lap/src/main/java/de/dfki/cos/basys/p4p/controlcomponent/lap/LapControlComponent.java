package de.dfki.cos.basys.p4p.controlcomponent.lap;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;

public class LapControlComponent extends BaseControlComponent<LapService> {

	public LapControlComponent(Properties config) {
		super(config);		
	}
	
	@Override
	protected void registerOperationModes() {		
		registerOperationMode(new ProjectRectangleOperationMode(this));		
	}
	
}
