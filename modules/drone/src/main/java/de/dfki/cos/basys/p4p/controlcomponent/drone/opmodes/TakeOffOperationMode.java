package de.dfki.cos.basys.p4p.controlcomponent.drone.opmodes;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
	
	private CountDownLatch counter;

	public TakeOffOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		
		counter = new CountDownLatch(1);

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

		getService(DroneService.class).takeOff();
		
		try {
			counter.await(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
