package de.dfki.cos.basys.p4p.controlcomponent.lap;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectRectangle", shortName = "PR_RECT", description = "projects a rectangle", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectRectangleOperationMode extends BaseLapOperationMode {

	@Parameter(name = "xRect", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "yRect", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "zRect", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "widthRect", direction = ParameterDirection.IN)
	private double width = 0;
	
	@Parameter(name = "heightRect", direction = ParameterDirection.IN)
	private double height = 0;
	
	@Parameter(name = "colorRect", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectRectangleOperationMode(BaseControlComponent<LapService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		getService(LapService.class).projectRectangle(x, y, z, color, width, height);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
