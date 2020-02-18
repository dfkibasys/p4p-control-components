package de.dfki.cos.basys.p4p.controlcomponent.worker;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

public class WorkerControlComponent extends BaseControlComponent<NotificationService> {

	public WorkerControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		registerOperationMode(new MoveToSymbolicPositionOperationMode(this));
		registerOperationMode(new RemoveObstacleOperationMode(this));
		registerOperationMode(new LoadOperationMode(this));
		registerOperationMode(new UnloadOperationMode(this));
	}
	
}
