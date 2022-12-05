package de.dfki.cos.basys.p4p.controlcomponent.mir.opmodes;

import de.dfki.cos.basys.common.rest.mir.MiRState;
import de.dfki.cos.basys.common.rest.mir.MirService;
import de.dfki.cos.basys.common.rest.mir.dto.Status;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import org.mockito.Mockito;

@OperationMode(name = "Pick", shortName = "PICK", description = "picks an object",
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE, ExecutionMode.AUTO })
public class PickOperationMode extends BaseOperationMode<MirService> {

	@Parameter(name = "pick_stationType", direction = ParameterDirection.IN)
	private String stationType = "floor-1";

	@Parameter(name = "pick_loadType", direction = ParameterDirection.IN)
	private String loadType = "EPAL";
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration = 0;

	private long startTime = 0;
	private long endTime = 0;

	public PickOperationMode(BaseControlComponent<MirService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
		try {			
			Status status = getService(MirService.class).setRobotStatus(MiRState.READY);	
			//TODO check status
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			component.setErrorStatus(3, e.getMessage());
			component.stop(component.getOccupierId());
		}
	}

	@Override
	public void onStarting() {		
		startTime = System.currentTimeMillis();
		try {
			//TODO: Implement starting behaviour
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			component.setErrorStatus(3, e.getMessage());
			component.stop(component.getOccupierId());
		}
	}

	@Override
	public void onExecute() {
		try {
			//TODO: Implement execution behaviour
			LOGGER.info("StationType: {}, LoadType: {}", stationType, loadType);
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			component.setErrorStatus(3, e.getMessage());
			component.stop(component.getOccupierId());
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
		try {
			Status status = getService(MirService.class).setRobotStatus(MiRState.PAUSED);
			//TODO: Implement stopping behaviour
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}	
	}

	@Override
	protected void configureServiceMock(MirService serviceMock) {
		Mockito.when(serviceMock.setRobotStatus(MiRState.READY)).thenReturn(new Status());
		Mockito.when(serviceMock.setRobotStatus(MiRState.PAUSED)).thenReturn(new Status());
		// Not much to do here since the real implementation is still mocking everything
	}
}
