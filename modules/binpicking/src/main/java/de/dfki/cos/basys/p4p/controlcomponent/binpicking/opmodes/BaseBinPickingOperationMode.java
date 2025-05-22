package de.dfki.cos.basys.p4p.controlcomponent.binpicking.opmodes;

import de.dfki.cos.basys.p4p.controlcomponent.binpicking.service.BinPickingService;
import de.dfki.cos.basys.p4p.controlcomponent.binpicking.service.BinPickingStatus;
import de.dfki.cos.basys.p4p.controlcomponent.binpicking.service.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.binpicking.service.WorkState;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;

public abstract class BaseBinPickingOperationMode extends BaseOperationMode<BinPickingService> {
	private static final Logger LOG = LoggerFactory.getLogger(BaseBinPickingOperationMode.class);
	private static final int MOCKUP_SERVICE_DURATION = 5000;
	
	protected long startTime = 0;
	protected long endTime = 0;
	protected int duration = 0;
	
	protected boolean executing = false;
	
	public BaseBinPickingOperationMode(BaseControlComponent<BinPickingService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
		getService(BinPickingService.class).reset();
		executing = false;
	}
	
	
	@Override
	public void onStarting() {
		startTime = System.currentTimeMillis();		
	}

	@Override
	public void onExecute() {
		MissionState state; WorkState wState;
		while(executing) {
			BinPickingService service = getService(BinPickingService.class);
			state = service.getMissionState();
			wState = service.getWorkState();
			component.setWorkState(wState.toString());
			LOG.info("Current mission state is {}.", state.getState().toString());
			LOG.info("Current work state is {}.", wState.getState().toString());
			switch(state.getState()) {
				case NONE:
					break;
				case EXECUTING:
					break;
				case DONE:
					executing=false;
					break;
				case FAILED:
					executing=false;
					component.setErrorStatus(1, "failed");
					component.stop(component.getOccupierId());
					break;
				case ABORTED:
					executing=false;
					component.setErrorStatus(2, "aborted");
					component.stop(component.getOccupierId());
					break;
			default:
				LOG.warn("Received unexpected mission state {}!", state.getState().toString());
				break;

			}
			sleep(500);
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
	protected void configureServiceMock(BinPickingService serviceMock) {
		Mockito.when(serviceMock.getWorkState()).thenReturn(WorkState.getInstance());
		Mockito.doNothing().when(serviceMock).reset();

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(BinPickingStatus.MState.EXECUTING);
			return null;
		}).when(serviceMock).provideParts(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString());

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(BinPickingStatus.MState.EXECUTING);
			return null;
		}).when(serviceMock).providePartsOrder(Mockito.any());

		Mockito.when(serviceMock.getMissionState()).thenAnswer(new Answer<MissionState>() {
			boolean accepted = false;
			@Override
			public MissionState answer(InvocationOnMock invocation) {
				if(!accepted)
				{
					MissionState.getInstance().setState(BinPickingStatus.MState.ACCEPTED);
					accepted = true;
				}
				else { // accepted
					long elapsed = System.currentTimeMillis() - startTime;
					if (elapsed < MOCKUP_SERVICE_DURATION) {
						MissionState.getInstance().setState(BinPickingStatus.MState.EXECUTING);
					} else {
						MissionState.getInstance().setState(BinPickingStatus.MState.DONE);
					}
			}
				return MissionState.getInstance();
			}
			
		});
	
	}
}
