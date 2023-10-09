package de.dfki.cos.basys.p4p.controlcomponent.workstation.opmodes;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.workstation.service.WorkstationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseWorkstationOperationMode extends BaseOperationMode<WorkstationService> {
	private static final Logger LOG = LoggerFactory.getLogger(BaseWorkstationOperationMode.class);
	private static final int MOCKUP_SERVICE_DURATION = 5000;
	
	protected long startTime = 0;
	protected long endTime = 0;
	protected int duration = 0;
	
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
		sleep(500);
		LOGGER.info("EXECUTE FINISHED");
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
