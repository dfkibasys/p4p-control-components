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

public abstract class BaseDroneOperationMode extends BaseOperationMode<DroneService> {
	private static final Logger LOG = LoggerFactory.getLogger(BaseDroneOperationMode.class);
	
	protected boolean executing = false;
	
	public BaseDroneOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		getService(DroneService.class).reset();
		executing = false;
	}

	@Override
	public void onExecute() {
		String state;
		while(executing) {
			DroneService service = getService(DroneService.class);
			state = service.getMissionState();
			component.setWorkState(service.getWorkState());
			LOG.debug("Current mission state is {}.", state);
			switch(state) {
				case "pending":
					break;
				case "executing":
					break;
				case "done":
					executing=false;
					break;
				case "failed":
					executing=false;
					component.setErrorStatus(1, "failed");
					component.stop(component.getOccupierId());
					break;
				case "aborted":
					executing=false;
					component.setErrorStatus(2, "aborted");
					component.stop(component.getOccupierId());
					break;
			default:
				LOG.warn("Received unexpected mission state {}!", state);
				break;

			}
			sleep(500);
		}
		
	}

	@Override
	public abstract void onCompleting();

	@Override
	public abstract void onStopping();
	
	@Override
	protected void configureServiceMock(DroneService serviceMock) {
	
	}
}
