package de.dfki.cos.basys.p4p.controlcomponent.drone;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;

public class DroneControlComponent extends BaseControlComponent<DroneService> {

	public DroneControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		registerOperationMode(new TakeOffOperationMode(this));	
		registerOperationMode(new MoveToSymbolicPositionOperationMode(this));	
		registerOperationMode(new ProvideVideoStreamOperationMode(this));	
		registerOperationMode(new DetectObstacleOperationMode(this));	
		registerOperationMode(new LandOperationMode(this));		
		registerOperationMode(new MoveToPointOperationMode(this));
	}
	
}
