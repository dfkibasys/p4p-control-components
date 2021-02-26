package de.dfki.cos.basys.p4p.controlcomponent.drone;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

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
		// #############################################################################
		// TODO we definitely need some sort of feedback (ret val, Exception, ...) here!
		getService(DroneService.class).takeOff();
		// #############################################################################
		executing = true;
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
