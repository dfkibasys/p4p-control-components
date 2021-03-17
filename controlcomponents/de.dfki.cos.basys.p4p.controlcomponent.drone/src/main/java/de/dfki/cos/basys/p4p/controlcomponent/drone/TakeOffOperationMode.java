package de.dfki.cos.basys.p4p.controlcomponent.drone;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MissionState;

@OperationMode(name = "TakeOff", shortName = "TAKEOFF", description = "brings the drone to flight height", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class TakeOffOperationMode extends BaseDroneOperationMode {
	private static final int NUM_RETRIES = 20;
	
	public TakeOffOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		for(int retry = 0; retry < NUM_RETRIES; retry++) {
			// #############################################################################
			// TODO we definitely need some sort of feedback (ret val, Exception, ...) here!
			getService(DroneService.class).takeOff();
			// #############################################################################
			sleep(500);
	
	
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
	public void onResetting() {
		super.onResetting();
		sleep(1000);
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		sleep(1000);
	}

	@Override
	public void onStopping() {
		super.onStopping();
		sleep(1000);
	}

}
