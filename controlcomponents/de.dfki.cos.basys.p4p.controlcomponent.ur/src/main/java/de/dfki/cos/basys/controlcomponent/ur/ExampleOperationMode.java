package de.dfki.cos.basys.controlcomponent.ur;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "example", shortName = "EX", description = "this is an example operation that "
		+ "calculates the length of the hypothenuse 'c' in a right triangle given sides 'a' and 'b' according to Pythagoras", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATION })
public class ExampleOperationMode extends BaseOperationMode {

	@Parameter(name = "side_a", direction = ParameterDirection.IN)
	private double a = 0;
	
	@Parameter(name = "side_b", direction = ParameterDirection.IN)
	private double b = 0;

	@Parameter(name = "hypothenuse_c", direction = ParameterDirection.OUT)
	private double c = 0;
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration = 0;
	
	@Parameter(name = "calculation", direction = ParameterDirection.OUT)
	private String calcString = "";
	
	long startTime = 0;
	long endTime = 0;
	
	
	public ExampleOperationMode(BaseControlComponent component) {
		super(component);
	}

	@Override
	public void onResetting() {
		c = 0;
		calcString = "";
		duration = 0;
		startTime = 0;
		endTime = 0;	
	}

	@Override
	public void onStarting() {		
		startTime = System.currentTimeMillis();		
		sleep(1000);
	}

	@Override
	public void onExecute() {
		c = Math.sqrt(a*a + b*b);
		calcString = "c² = a² + b² with a=" + a + ", b=" + b + ", and c=" + c;
		sleep(1000);
	}

	@Override
	public void onCompleting() {
		endTime = System.currentTimeMillis();
		duration = (int) (endTime - startTime);
		sleep(1000);
	}

	@Override
	public void onStopping() {
		endTime = System.currentTimeMillis();
		duration = (int) (endTime - startTime);
		sleep(1000);
	}

	public void sleep(long millis) {
		try {
			TimeUnit.MILLISECONDS.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
