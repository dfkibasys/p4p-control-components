package de.dfki.cos.basys.p4p.controlcomponent.drone;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DronePoint;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionStateListener;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MState;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "MovePoint", shortName = "MOVE_PNT", description = "moves component to a point", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class MoveToPointOperationMode extends BaseDroneOperationMode{
	
	CountDownLatch counter = new CountDownLatch(1);

	@Parameter(name = "x", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "y", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "z", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "rot", direction = ParameterDirection.IN)
	private double rot = 0;
	
	@Parameter(name = "pitch", direction = ParameterDirection.IN)
	private double pitch = 0;
	
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
					counter.countDown();
				}
				else if (newState.equals(MState.REJECTED)) {
					getService(DroneService.class).moveToPoint(point);
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
		sleep(1000);
	}

	@Override
	public void onStopping() {
		super.onStopping();
		sleep(1000);
	}
}
