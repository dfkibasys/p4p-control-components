package de.dfki.cos.basys.p4p.controlcomponent.baxter;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "RemoveObstacle", shortName = "REMOVE", description = "remove obstacle", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATION })
public class RemoveObstacleOperationMode extends BaseOperationMode<BaxterService> {

	@Parameter(name = "grid", direction = ParameterDirection.IN)
	private String grid = "G10:H11";
	
	@Parameter(name = "type", direction = ParameterDirection.IN)
	private String type = "block";	
	
		
	public RemoveObstacleOperationMode(BaseControlComponent<BaxterService> component) {
		super(component);
	}
	
	@Override
	public void onResetting() {
		sleep(1000);
	}

	@Override
	public void onStarting() {
		int x = 0;
		int y = 0;
		//getService(BaxterService.class).removeObstacle(type, x, y);
		sleep(1000);
	}

	@Override
	public void onExecute() {
		sleep(1000);		
	}

	@Override
	public void onCompleting() {
		sleep(1000);
	}

	@Override
	public void onStopping() {
		sleep(1000);
	}

	@Override
	protected void configureServiceMock(BaxterService serviceMock) {
	
	}
}
