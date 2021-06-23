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

@OperationMode(name = "ProjectArrowsAndCircle", shortName = "PR_ARCI", description = "projects a arrows and circles", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProjectArrowsAndCirclesOperationMode extends BaseLapOperationMode {

	@Parameter(name = "xArci", direction = ParameterDirection.IN)
	private double x = 0;
	
	@Parameter(name = "yArci", direction = ParameterDirection.IN)
	private double y = 0;
	
	@Parameter(name = "zArci", direction = ParameterDirection.IN)
	private double z = 0;
	
	@Parameter(name = "colorArci", direction = ParameterDirection.IN)
	private int color = 0;
	
	@Parameter(name = "pointsArci", direction = ParameterDirection.IN)
	private String points = "";
	
	@Parameter(name = "arrowCountArci", direction = ParameterDirection.IN)
	private int arrowCount = 0;
	
	@Parameter(name = "innerCircleRadiusArci", direction = ParameterDirection.IN)
	private double innerCircleRadius = 0;
	
	@Parameter(name = "middleCircleRadiusArci", direction = ParameterDirection.IN)
	private double middleCircleRadius = 0;
	
	@Parameter(name = "outerCircleRadiusArci", direction = ParameterDirection.IN)
	private double outerCircleRadius = 0;
	
	@Parameter(name = "angleStartArci", direction = ParameterDirection.IN)
	private double angleStart = 0;
	
	@Parameter(name = "angleLengthArci", direction = ParameterDirection.IN)
	private double angleLength = 0;
	
	@Parameter(name = "delayArrowsArci", direction = ParameterDirection.IN)
	private int delayArrows = 0;
	
	@Parameter(name = "delayCirclesArci", direction = ParameterDirection.IN)
	private int delayCircles = 0;
	
	@Parameter(name = "durationArci", direction = ParameterDirection.OUT)
	private int duration_out = 0;
	
		
	public ProjectArrowsAndCirclesOperationMode(BaseControlComponent<LapService> component) {
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
		
		getService(LapService.class).projectArrowsAndCircles(x, y, z, color, pnts, arrowCount, innerCircleRadius, middleCircleRadius, outerCircleRadius, angleStart, angleLength, delayArrows, delayCircles);
		executing = true;
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
	}

}
