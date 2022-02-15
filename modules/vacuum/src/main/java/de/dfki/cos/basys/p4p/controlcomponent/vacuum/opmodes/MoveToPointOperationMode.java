package de.dfki.cos.basys.p4p.controlcomponent.vacuum.opmodes;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.VacuumService;
import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.Vector2d;
import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.MissionStateListener;
import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.VacuumStatus.MState;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "MovePoint", shortName = "MOVE_PNT", description = "moves component to a point", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class MoveToPointOperationMode extends BaseVacuumOperationMode{
	
	private CountDownLatch counter;

	@Parameter(name = "x", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "y", direction = ParameterDirection.IN)
	private double y = 0;
	
	
	public MoveToPointOperationMode(BaseControlComponent<VacuumService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		
		counter = new CountDownLatch(1);
	
		Vector2d point = new Vector2d(x, y);
		
		MissionState.getInstance().addStateListener(new MissionStateListener() {

			@Override
			public void stateChangedEvent(MState oldState, MState newState) {
				if (newState.equals(MState.ACCEPTED) || newState.equals(MState.EXECUTING)) {
					executing = true;
					component.setErrorStatus(0, "OK");
					counter.countDown();
				}
				else if (newState.equals(MState.REJECTED)) {
					component.setErrorStatus(3, "rejected");
					counter.countDown();
				}
			}
			
		});
		
		// precautionary set timeout error (gets overridden in case of success)
		component.setErrorStatus(4, "timeout");		
		
		getService(VacuumService.class).moveToPoint(point);
		
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
