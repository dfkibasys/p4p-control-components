package de.dfki.cos.basys.p4p.controlcomponent.lap;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectMovingETA", shortName = "PR_META", description = "projects a moving ETA", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectMovingETAOperationMode extends BaseLapOperationMode {

	@Parameter(name = "xMeta", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "yMeta", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "zMeta", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "colorMeta", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "radiusMeta", direction = ParameterDirection.IN)
	private double radius = 0;
	
	@Parameter(name = "angleMeta", direction = ParameterDirection.IN)
	private double angle = 0;
	
	@Parameter(name = "fullTimeMeta", direction = ParameterDirection.IN)
	private double fullTime = 0;
	
	@Parameter(name = "startTimeMeta", direction = ParameterDirection.IN)
	private double startTime = 0;
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectMovingETAOperationMode(BaseControlComponent<LapService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		getService(LapService.class).projectMovingETA(x, y, z, color, radius, angle, fullTime, startTime);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
