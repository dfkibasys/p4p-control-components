package de.dfki.cos.basys.p4p.controlcomponent.drone;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MState;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionStateListener;

@OperationMode(name = "MoveSymbolic", shortName = "MOVE_SYM", description = "moves component to a symbolic position", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class MoveToSymbolicPositionOperationMode extends BaseDroneOperationMode {
	
	CountDownLatch counter = new CountDownLatch(1);

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

		getService(DroneService.class).moveToSymbolicPosition(position);
			
		MissionState.getInstance().addStateListener(new MissionStateListener() {

			@Override
			public void stateChangedEvent(MState oldState, MState newState) {
				if (newState.equals(MState.ACCEPTED) || newState.equals(MState.EXECUTING)) {
					executing = true;
					counter.countDown();
				}
				else if (newState.equals(MState.REJECTED)) {
					getService(DroneService.class).moveToSymbolicPosition(position);
				}
			}
			
		});
		
		try {
			counter.await(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		MissionState.getInstance().removeStateListeners();
		duration_out = duration;
		sleep(1000);
	}

	@Override
	public void onStopping() {
		super.onStopping();
		sleep(1000);
	}

}
