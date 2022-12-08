package de.dfki.cos.basys.p4p.controlcomponent.mir.opmodes;

import de.dfki.cos.basys.common.rest.mir.MiRState;
import de.dfki.cos.basys.common.rest.mir.MirService;
import de.dfki.cos.basys.common.rest.mir.dto.MissionInstanceInfo;
import de.dfki.cos.basys.common.rest.mir.dto.Status;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@OperationMode(name = "Drop", shortName = "DROP", description = "drops an object",
		allowedCommands = {	ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP },
		allowedModes = { ExecutionMode.SIMULATE})
public class DropOperationMode extends BaseMiROperationMode {

	@Parameter(name = "drop_stationType", direction = ParameterDirection.IN)
	private String stationType = "floor-1";

	@Parameter(name = "drop_loadType", direction = ParameterDirection.IN)
	private String loadType = "EPAL";

	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;

	public DropOperationMode(BaseControlComponent<MirService> component) {
		super(component);
	}


	@Override
	public void onStarting() {
		super.onStarting();
		currentMission = getService(MirService.class).drop(stationType, loadType);
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