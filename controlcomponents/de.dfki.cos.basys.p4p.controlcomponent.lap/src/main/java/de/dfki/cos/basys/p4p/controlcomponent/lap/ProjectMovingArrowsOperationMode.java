package de.dfki.cos.basys.p4p.controlcomponent.lap;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto.Point;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "ProjectMovingArrows", shortName = "PR_MVAR", description = "projects moving arrows", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectMovingArrowsOperationMode extends BaseLapOperationMode {

	@Parameter(name = "xMvar", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "yMvar", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "zMvar", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "pointsMvar", direction = ParameterDirection.IN)
	private String points = "";
	
	@Parameter(name = "arrowCountMvar", direction = ParameterDirection.IN)
	private int arrowCount = 0;
	
	@Parameter(name = "delayMvar", direction = ParameterDirection.IN)
	private int delay = 0;
	
	@Parameter(name = "colorMvar", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "durationMvar", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectMovingArrowsOperationMode(BaseControlComponent<LapService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		
		// convert JSON string to List<Point>
		List<Point> pnts = null;
		try {
			pnts = new ObjectMapper().readValue(points, new TypeReference<List<Point>>() {});
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		getService(LapService.class).projectMovingArrows(x, y, z, color, pnts, arrowCount, delay);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
