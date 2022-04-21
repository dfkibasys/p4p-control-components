package de.dfki.cos.basys.p4p.controlcomponent.drone.opmodes;

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
	
	private CountDownLatch counter;

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
		
		counter = new CountDownLatch(1);
	
		DronePoint point = new DronePoint(x, y, z, rot, pitch);
		
		MissionState.getInstance().addStateListener((oldState, newState) -> {
			if (newState.equals(MState.ACCEPTED) || newState.equals(MState.EXECUTING)) {
				executing = true;
				component.setErrorStatus(0, "OK");
				counter.countDown();
			}
			else if (newState.equals(MState.REJECTED)) {
				component.setErrorStatus(3, "rejected");
				counter.countDown();
			}
		});
		
		// precautionary set timeout error (gets overridden in case of success)
		component.setErrorStatus(4, "timeout");		
		
		getService(DroneService.class).moveToPoint(point);
		
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
		// remove listeners in case of success
		MissionState.getInstance().removeStateListeners();
		sleep(1000);
	}

	@Override
	public void onStopping() {
		super.onStopping();
		// remove listeners in case of error
		MissionState.getInstance().removeStateListeners();
		sleep(1000);
	}
}
