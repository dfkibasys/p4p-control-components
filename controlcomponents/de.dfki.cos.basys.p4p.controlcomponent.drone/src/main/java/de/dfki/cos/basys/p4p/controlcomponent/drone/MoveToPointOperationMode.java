package de.dfki.cos.basys.p4p.controlcomponent.drone;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DronePoint;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionStateListener;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MState;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "MovePoint", shortName = "MOVE_PNT", description = "moves component to a point", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class MoveToPointOperationMode extends BaseDroneOperationMode{

	@Parameter(name = "x", direction = ParameterDirection.IN)
	private float x = 0.0f;
	
	@Parameter(name = "y", direction = ParameterDirection.IN)
	private float y = 0.0f;
	
	@Parameter(name = "z", direction = ParameterDirection.IN)
	private float z = 0.0f;
	
	@Parameter(name = "rot", direction = ParameterDirection.IN)
	private float rot = 0.0f;
	
	@Parameter(name = "pitch", direction = ParameterDirection.IN)
	private float pitch = 0.0f;
	
	public MoveToPointOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
	
		DronePoint point = new DronePoint(x, y, z, rot, pitch);
		
		getService(DroneService.class).moveToPoint(point);
		
		MissionState.getInstance().addStateListener(new MissionStateListener() {

			@Override
			public void stateChangedEvent(MState oldState, MState newState) {
				if (newState.equals(MState.ACCEPTED) || newState.equals(MState.EXECUTING)) {
					executing = true;
				}
				else if (newState.equals(MState.REJECTED) || newState.equals(MState.PENDING)) {
					executing = false;	
					getService(DroneService.class).moveToPoint(point);
				}
			}
			
		});
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
