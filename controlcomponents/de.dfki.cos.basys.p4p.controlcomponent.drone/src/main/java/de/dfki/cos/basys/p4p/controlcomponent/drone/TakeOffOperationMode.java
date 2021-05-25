package de.dfki.cos.basys.p4p.controlcomponent.drone;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionStateListener;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MState;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionState;

@OperationMode(name = "TakeOff", shortName = "TAKEOFF", description = "brings the drone to flight height", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class TakeOffOperationMode extends BaseDroneOperationMode {

	public TakeOffOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();

		getService(DroneService.class).takeOff();
		
		MissionState.getInstance().addStateListener(new MissionStateListener() {

			@Override
			public void stateChangedEvent(MState oldState, MState newState) {
				if (newState.equals(MState.ACCEPTED) || newState.equals(MState.EXECUTING)) {
					executing = true;
				}
				else if (newState.equals(MState.REJECTED)) {
					executing = false;	
					getService(DroneService.class).takeOff();
				}
			}
			
		});
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
