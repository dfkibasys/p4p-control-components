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

	@Parameter(name = "xPuci", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "yPuci", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "zPuci", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "innerCircleRadiusPuci", direction = ParameterDirection.IN)
	private double innerCircleRadius = 0;
	
	@Parameter(name = "middleCircleRadiusPuci", direction = ParameterDirection.IN)
	private double middleCircleRadius = 0;
	
	@Parameter(name = "outerCircleRadiusPuci", direction = ParameterDirection.IN)
	private double outerCircleRadius = 0;
	
	@Parameter(name = "angleStartPuci", direction = ParameterDirection.IN)
	private double angleStart = 0;
	
	@Parameter(name = "angleLengthPuci", direction = ParameterDirection.IN)
	private double angleLength = 0;
	
	@Parameter(name = "delayPuci", direction = ParameterDirection.IN)
	private int delay = 0;
	
	@Parameter(name = "colorPuci", direction = ParameterDirection.IN)
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
