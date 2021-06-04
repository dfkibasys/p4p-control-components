package de.dfki.cos.basys.p4p.controlcomponent.lap;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;
import de.dfki.cos.basys.common.rest.mir.MirService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectRectangle", shortName = "PR_RECT", description = "projects a rectangle", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectRectangleOperationMode extends BaseOperationMode<LapService> {

	@Parameter(name = "x", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "y", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "z", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "width", direction = ParameterDirection.IN)
	private double width = 0;
	
	@Parameter(name = "height", direction = ParameterDirection.IN)
	private double height = 0;
	
	@Parameter(name = "color", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration = 0;
	
	private long startTime = 0;
	private long endTime = 0;
	
	private String missionState;
		
	public ProjectRectangleOperationMode(BaseControlComponent<LapService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
	}

	@Override
	public void onStarting() {	
		startTime = System.currentTimeMillis();	
		getService(LapService.class).projectRectangle(x, y, z, width, height, color);

	}

	@Override
	public void onExecute() {
		try {
			boolean executing = true;
			while(executing) {
				missionState = getService(LapService.class).getMissionState();
				 
				switch (missionState) {
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
					break;
				}
	
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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
	}

	@Override
	protected void configureServiceMock(LapService serviceMock) {
		//TODO
	}
}
