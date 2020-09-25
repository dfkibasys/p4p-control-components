package de.dfki.cos.basys.p4p.controlcomponent.wallet;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import edu.wpi.rail.jrosbridge.Goal.GoalStatusEnum;
import edu.wpi.rail.jrosbridge.messages.actionlib.GoalStatus;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "MoveSymbolic", shortName = "MOVE_SYM", description = "moves component to a symbolic position", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATION })
public class MoveToSymbolicPositionOperationMode extends BaseOperationMode<WalletService> {

	@Parameter(name = "position", direction = ParameterDirection.IN)
	private String position = "";
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration = 0;
	
	private long startTime = 0;
	private long endTime = 0;
		
	public MoveToSymbolicPositionOperationMode(BaseControlComponent<WalletService> component) {
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
		getService(WalletService.class).gotoSymbolicPosition(position);
	}

	@Override
	public void onExecute() {
		boolean executing = true;
		while(executing) {
			GoalStatusEnum status = getService(WalletService.class).getStatus();
			LOGGER.debug("Status : " + status);
			 
			switch (status) {
			case ABORTED:
				component.setErrorStatus(4, "ABORTED");
				executing = false;
				break;
			case ACTIVE:
				break;
			case LOST:
				component.setErrorStatus(9, "LOST");
				executing = false;
				break;
			case PENDING:
				break;
			case PREEMPTED:
				component.setErrorStatus(2, "PREEMPTED");	
				executing = false;			
				break;
			case PREEMPTING:
				break;
			case RECALLED:
				component.setErrorStatus(8, "RECALLED");
				executing = false;
				break;
			case RECALLING:
				break;
			case REJECTED:
				component.setErrorStatus(5, "REJECTED");
				executing = false;
				break;
			case SUCCEEDED:
				executing = false;
				break;
			default:
				break;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (component.getErrorCode() > 0) {
			component.stop(component.getOccupierId());
		}
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
	
	@Override
	protected void configureServiceMock(WalletService serviceMock) {
	
	}
}