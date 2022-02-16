package de.dfki.cos.basys.p4p.controlcomponent.laplaser.opmodes;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.laplaser.service.LapLaserService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectLine", shortName = "PR_LINE", description = "projects a line", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectLineOperationMode extends BaseLapOperationMode {

	@Parameter(name = "xLine", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "yLine", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "zLine", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "x2Line", direction = ParameterDirection.IN)
	private double x2 = 0;
	
	@Parameter(name = "y2Line", direction = ParameterDirection.IN)
	private double y2 = 0;
	
	@Parameter(name = "z2Line", direction = ParameterDirection.IN)
	private double z2 = 0;
	
	@Parameter(name = "colorLine", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "durationLine", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectLineOperationMode(BaseControlComponent<LapLaserService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		getService(LapLaserService.class).projectLine(x, y, z, color, x2, y2, z2);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
