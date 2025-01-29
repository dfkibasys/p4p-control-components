package de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.opmodes;

import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.SmartwatchStatus.*;
import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.TaskState;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.SmartwatchService;

public abstract class BaseSmartwatchOperationMode extends BaseOperationMode<SmartwatchService> {
	protected static final Logger LOG = LoggerFactory.getLogger(BaseSmartwatchOperationMode.class);
	private static final int MOCKUP_SERVICE_DURATION = 5000;
	
	protected long startTime = 0;
	protected long endTime = 0;
	protected int duration = 0;
	
	protected boolean executing = false;
	
	public BaseSmartwatchOperationMode(BaseControlComponent<SmartwatchService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
		getService(SmartwatchService.class).reset();
		executing = false;
	}
	
	
	@Override
	public void onStarting() {
		startTime = System.currentTimeMillis();		
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
		getService(SmartwatchService.class).reset();
		sleep(1000);
	}
	
	@Override
	protected void configureServiceMock(SmartwatchService serviceMock) {
		Mockito.when(serviceMock.getTaskState()).thenReturn(TaskState.getInstance());
		Mockito.doNothing().when(serviceMock).reset();

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			TaskState.getInstance().setState(TState.EXECUTING);
			return null;
		}).when(serviceMock).requestTaskExecution(Mockito.any());

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			TaskState.getInstance().setState(TState.EXECUTING);
			return null;
		}).when(serviceMock).displayInfoMessage(Mockito.any());

		Mockito.when(serviceMock.getTaskState()).thenAnswer(new Answer<TaskState>() {
			boolean pending = false;
			@Override
			public TaskState answer(InvocationOnMock invocation) {
				if(!pending)
				{
					TaskState.getInstance().setState(TState.PENDING);
					pending = true;
				}
				else { // pending
					long elapsed = System.currentTimeMillis() - startTime;
					if (elapsed < MOCKUP_SERVICE_DURATION) {
						TaskState.getInstance().setState(TState.EXECUTING);
					} else {
						TaskState.getInstance().setState(TState.DONE);
					}
				}
				return TaskState.getInstance();
			}

		});

	
	}
}
