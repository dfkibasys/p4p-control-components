package de.dfki.cos.basys.p4p.controlcomponent.lap;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectChar", shortName = "PR_CHAR", description = "projects a char", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectCharOperationMode extends BaseLapOperationMode {

	@Parameter(name = "x", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "y", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "z", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "width", direction = ParameterDirection.IN)
	private char chr;
	
	@Parameter(name = "height", direction = ParameterDirection.IN)
	private double height = 0;
	
	@Parameter(name = "color", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectCharOperationMode(BaseControlComponent<LapService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		getService(LapService.class).projectChar(x, y, z, color, chr, height);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
