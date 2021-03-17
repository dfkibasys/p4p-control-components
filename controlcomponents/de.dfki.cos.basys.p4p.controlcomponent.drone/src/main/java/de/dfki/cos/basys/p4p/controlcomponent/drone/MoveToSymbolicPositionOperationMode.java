package de.dfki.cos.basys.p4p.controlcomponent.drone;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MissionState;

@OperationMode(name = "MoveSymbolic", shortName = "MOVE_SYM", description = "moves component to a symbolic position", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class MoveToSymbolicPositionOperationMode extends BaseDroneOperationMode {
	private static final int NUM_RETRIES = 20;

	@Parameter(name = "position", direction = ParameterDirection.IN)
	private String position = "";
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public MoveToSymbolicPositionOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		for(int retry = 0; retry < NUM_RETRIES; retry++) {
			// #############################################################################
			// TODO we definitely need some sort of feedback (ret val, Exception, ...) here!
			getService(DroneService.class).moveToSymbolicPosition(position);
			// #############################################################################
			sleep(1000);
	
			//TODO: Improve this code
			if(getService(DroneService.class).getMissionState().equals(MissionState.ACCEPTED) || 
			getService(DroneService.class).getMissionState().equals(MissionState.EXECUTING))
			{
				executing = true;
				break;
			}
			else if(getService(DroneService.class).getMissionState().equals(MissionState.REJECTED))
			{
				executing = false;
			}
			sleep(1500);
		}
		
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
		sleep(1000);
	}

	@Override
	public void onStopping() {
		super.onStopping();
		sleep(1000);
	}

}
