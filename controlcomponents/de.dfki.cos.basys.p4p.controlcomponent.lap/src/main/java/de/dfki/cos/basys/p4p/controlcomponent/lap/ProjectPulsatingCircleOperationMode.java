package de.dfki.cos.basys.p4p.controlcomponent.lap;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectPulsatingCircle", shortName = "PR_PUCI", description = "projects a pulsating circle", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectPulsatingCircleOperationMode extends BaseLapOperationMode {

	@Parameter(name = "x", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "y", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "z", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "innerCircleRadius", direction = ParameterDirection.IN)
	private double innerCircleRadius = 0;
	
	@Parameter(name = "middleCircleRadius", direction = ParameterDirection.IN)
	private double middleCircleRadius = 0;
	
	@Parameter(name = "outerCircleRadius", direction = ParameterDirection.IN)
	private double outerCircleRadius = 0;
	
	@Parameter(name = "angleStart", direction = ParameterDirection.IN)
	private double angleStart = 0;
	
	@Parameter(name = "angleLength", direction = ParameterDirection.IN)
	private double angleLength = 0;
	
	@Parameter(name = "delay", direction = ParameterDirection.IN)
	private int delay = 0;
	
	@Parameter(name = "color", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectPulsatingCircleOperationMode(BaseControlComponent<LapService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		getService(LapService.class).projectPulsatingCircle(x, y, z, color, innerCircleRadius, middleCircleRadius, outerCircleRadius, angleStart, angleLength, delay);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
