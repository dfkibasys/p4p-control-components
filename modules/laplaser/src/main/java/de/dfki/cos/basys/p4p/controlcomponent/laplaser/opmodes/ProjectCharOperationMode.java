package de.dfki.cos.basys.p4p.controlcomponent.laplaser.opmodes;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.laplaser.service.LapLaserService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectChar", shortName = "PR_CHAR", description = "projects a char", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectCharOperationMode extends BaseLapOperationMode {

	@Parameter(name = "xChar", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "yChar", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "zChar", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "chrChar", direction = ParameterDirection.IN)
	private String chr;
	
	@Parameter(name = "heightChar", direction = ParameterDirection.IN)
	private double height = 0;
	
	@Parameter(name = "colorChar", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "durationChar", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectCharOperationMode(BaseControlComponent<LapLaserService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		
		char c = chr.charAt(0);
		
		getService(LapLaserService.class).projectChar(x, y, z, color, c, height);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
