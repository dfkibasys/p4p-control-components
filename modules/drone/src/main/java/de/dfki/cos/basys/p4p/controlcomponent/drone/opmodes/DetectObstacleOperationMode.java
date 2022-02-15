package de.dfki.cos.basys.p4p.controlcomponent.drone.opmodes;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "DetectObstacles", shortName = "DETECT", description = "detect nearby obstacles", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class DetectObstacleOperationMode extends BaseDroneOperationMode {
	@Parameter(name = "type", direction = ParameterDirection.IN)
	private String type = "block";	
	

	public DetectObstacleOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {
		super.onStarting();
		// Start Video Streaming with endpoint of obstacle detection service
		getService(DroneService.class).startLiveImage();
		sleep(1000);
		getService(DroneService.class).detectObstacles(type);
		executing = true;
		
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		// TODO Report retrieved set of obstacles to Worldmodel server
		sleep(1000);
		// Stop video stream
		getService(DroneService.class).stopLiveImage();
		getService(DroneService.class).reset();
		
	}

	@Override
	public void onStopping() {	
		super.onStopping();
		getService(DroneService.class).stopLiveImage();
		getService(DroneService.class).reset();		
	}
}
