package de.dfki.cos.basys.p4p.controlcomponent.drone;

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

@OperationMode(name = "MoveSymbolic", shortName = "MVSYM", description = "moves Baxter to a symbolic position", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATION })
public class MoveToSymbolicPositionOperationMode extends BaseOperationMode<DroneService> {

	@Parameter(name = "position", direction = ParameterDirection.IN)
	private String position = "Station-Wait";
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration = 0;
	
	private long startTime = 0;
	private long endTime = 0;
		
	public MoveToSymbolicPositionOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
	}

	@Override
	public void onStarting() {		
		startTime = System.currentTimeMillis();	
	}

	@Override
	public void onExecute() {
		
	}

	@Override
	public void onCompleting() {
		endTime = System.currentTimeMillis();
		duration = (int) (endTime - startTime);
	}

	@Override
	public void onStopping() {
		endTime = System.currentTimeMillis();
		duration = (int) (endTime - startTime);
	}

	public void sleep(long millis) {
		try {
			TimeUnit.MILLISECONDS.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void configureServiceMock(DroneService serviceMock) {
	
	}
}
