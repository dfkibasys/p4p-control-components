package de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.opmodes;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.service.WorkstationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseWorkstationOperationMode extends BaseOperationMode<WorkstationService> {
	private static final Logger LOG = LoggerFactory.getLogger(BaseWorkstationOperationMode.class);
	private static final int MOCKUP_SERVICE_DURATION = 5000;
	
	protected long startTime = 0;
	protected long endTime = 0;
	protected int duration = 0;

	protected boolean executing = false;
	
	public BaseWorkstationOperationMode(BaseControlComponent<WorkstationService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
		getService(WorkstationService.class).reset();
	}

	@Override
	public void onStarting() {
		startTime = System.currentTimeMillis();
	}

	@Override
	public void onExecute() {
		MissionState state;
		while(executing) {
			WorkstationService service = getService(WorkstationService.class);
			state = service.getMissionState();
			LOG.debug("Current mission state is {}.", state);
			switch(state.getState()) {
				case PENDING:
					break;
				case EXECUTING:
					break;
				case DONE:
					executing=false;
					break;
				case FAILED:
					executing=false;
					component.setErrorStatus(1, "failed");
					component.stop(component.getOccupierId());
					break;
				case ABORTED:
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
	public void onCompleting() {
		endTime = System.currentTimeMillis();
		duration = (int) (endTime - startTime);
	}

	@Override
	public void onStopping() {
		endTime = System.currentTimeMillis();
		duration = (int) (endTime - startTime);
	}
	
	@Override
	protected void configureServiceMock(WorkstationService serviceMock) {
		// TODO
	}
}
