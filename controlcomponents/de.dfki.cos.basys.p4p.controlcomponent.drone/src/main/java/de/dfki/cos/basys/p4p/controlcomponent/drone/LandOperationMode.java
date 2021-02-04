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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "Land", shortName = "LAND", description = "lands the drone", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATION })
public class LandOperationMode extends BaseDroneOperationMode{
	private static final Logger LOG = LoggerFactory.getLogger(LandOperationMode.class);
	
	public LandOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		// #############################################################################
		// TODO we definitely need some sort of feedback (ret val, Exception, ...) here!
		getService(DroneService.class).land();
		// #############################################################################
		executing = true;
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
	}
}
