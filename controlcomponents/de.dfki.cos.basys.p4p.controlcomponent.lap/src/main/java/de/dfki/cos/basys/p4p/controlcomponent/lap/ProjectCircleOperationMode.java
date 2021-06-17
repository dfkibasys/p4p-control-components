package de.dfki.cos.basys.p4p.controlcomponent.lap;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectCircle", shortName = "PR_CIRC", description = "projects a circle", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectCircleOperationMode extends BaseLapOperationMode {

	@Parameter(name = "xCirc", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "yCirc", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "zCirc", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "radiusCirc", direction = ParameterDirection.IN)
	private double radius = 0;
	
	@Parameter(name = "angleStartCirc", direction = ParameterDirection.IN)
	private double angleStart = 0;
	
	@Parameter(name = "angleLengthCirc", direction = ParameterDirection.IN)
	private double angleLength = 0;
	
	@Parameter(name = "colorCirc", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectCircleOperationMode(BaseControlComponent<LapService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		getService(LapService.class).projectCircle(x, y, z, color, radius, angleStart, angleLength);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
