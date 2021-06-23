package de.dfki.cos.basys.p4p.controlcomponent.lap;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectString", shortName = "PR_STRG", description = "projects a string", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectStringOperationMode extends BaseLapOperationMode {

	@Parameter(name = "xStrg", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "yStrg", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "zStrg", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "textStrg", direction = ParameterDirection.IN)
	private String text = "";
	
	@Parameter(name = "heightStrg", direction = ParameterDirection.IN)
	private double height = 0;
	
	@Parameter(name = "colorStrg", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "durationStrg", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectStringOperationMode(BaseControlComponent<LapService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		getService(LapService.class).projectString(x, y, z, color, text, height);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
