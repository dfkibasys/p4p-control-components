package de.dfki.cos.basys.p4p.controlcomponent.drone;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionStateListener;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MState;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProvideVideoStream", shortName = "PROVIDEO", description = "provides a video stream", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProvideVideoStreamOperationMode extends BaseDroneOperationMode {

	public ProvideVideoStreamOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();

		getService(DroneService.class).startLiveImage();
	
		MissionState.getInstance().addStateListener(new MissionStateListener() {

			@Override
			public void stateChangedEvent(MState oldState, MState newState) {
				if (newState.equals(MState.ACCEPTED) || newState.equals(MState.EXECUTING)) {
					executing = true;
				}
				else if (newState.equals(MState.REJECTED)) {
					executing = false;	
					getService(DroneService.class).startLiveImage();
				}
			}
			
		});
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		getService(DroneService.class).reset();
	}

	@Override
	public void onStopping() {
		super.onStopping();
		getService(DroneService.class).reset();
		getService(DroneService.class).stopLiveImage();
		
	}
}
