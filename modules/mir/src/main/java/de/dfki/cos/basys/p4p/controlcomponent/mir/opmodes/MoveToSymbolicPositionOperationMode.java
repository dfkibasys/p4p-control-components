package de.dfki.cos.basys.p4p.controlcomponent.mir.opmodes;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.common.rest.mir.MiRState;
import de.dfki.cos.basys.common.rest.mir.MirService;
import de.dfki.cos.basys.common.rest.mir.dto.MissionInstanceInfo;
import de.dfki.cos.basys.common.rest.mir.dto.Status;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "MoveSymbolic", shortName = "MVSYM", description = "moves MiR to a symbolic position", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP },
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE, ExecutionMode.AUTO })
public class MoveToSymbolicPositionOperationMode extends BaseMiROperationMode {

	@Parameter(name = "position", direction = ParameterDirection.IN)
	private String position = "Waypoint01";
	
	@Parameter(name = "move_duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;

	public MoveToSymbolicPositionOperationMode(BaseControlComponent<MirService> component) {
		super(component);
	}

	@Override
	public void onStarting() {
		super.onStarting();
		currentMission = getService(MirService.class).gotoSymbolicPosition(position);
		sleep(1000);
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
		sleep(1000);
	}

	@Override
	public void onStopping() {
		super.onStopping();
		sleep(1000);
	}
}
