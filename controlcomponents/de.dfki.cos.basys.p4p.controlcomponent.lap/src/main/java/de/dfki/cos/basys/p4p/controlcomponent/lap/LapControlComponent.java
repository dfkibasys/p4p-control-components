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
		registerOperationMode(new ProjectArrowsAndCirclesOperationMode(this));		
		registerOperationMode(new ProjectCharOperationMode(this));		
		registerOperationMode(new ProjectCircleOperationMode(this));		
		registerOperationMode(new ProjectEllipseOperationMode(this));		
		registerOperationMode(new ProjectLineOperationMode(this));		
		registerOperationMode(new ProjectMovingArrowsOperationMode(this));		
		registerOperationMode(new ProjectMovingETAOperationMode(this));		
		registerOperationMode(new ProjectPulsatingCircleOperationMode(this));		
		registerOperationMode(new ProjectRectangleOperationMode(this));		
		registerOperationMode(new ProjectStringOperationMode(this));		
		registerOperationMode(new StopProjectionOperationMode(this));		
	}
	
}
