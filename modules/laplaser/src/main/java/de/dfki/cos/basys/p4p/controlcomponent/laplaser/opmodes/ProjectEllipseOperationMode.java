package de.dfki.cos.basys.p4p.controlcomponent.laplaser.opmodes;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.laplaser.service.LapService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectEllipse", shortName = "PR_ELPS", description = "projects an ellipse", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectEllipseOperationMode extends BaseLapOperationMode {

	@Parameter(name = "xElps", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "yElps", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "zElps", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "majorRadiusElps", direction = ParameterDirection.IN)
	private double majorRadius = 0;
	
	@Parameter(name = "minorRadiusElps", direction = ParameterDirection.IN)
	private double minorRadius = 0;
	
	@Parameter(name = "angleStartElps", direction = ParameterDirection.IN)
	private double angleStart = 0;
	
	@Parameter(name = "angleLengthElps", direction = ParameterDirection.IN)
	private double angleLength = 0;
	
	@Parameter(name = "colorElps", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "durationElps", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectEllipseOperationMode(BaseControlComponent<LapService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		getService(LapService.class).projectEllipse(x, y, z, color, majorRadius, minorRadius, angleStart, angleLength);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
