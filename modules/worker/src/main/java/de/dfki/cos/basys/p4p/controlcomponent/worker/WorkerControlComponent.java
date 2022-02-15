package de.dfki.cos.basys.p4p.controlcomponent.worker;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.worker.opmodes.LoadOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.worker.opmodes.MoveToSymbolicPositionOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.worker.opmodes.RemoveObstacleOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.worker.opmodes.UnloadOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.worker.service.NotificationService;

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
