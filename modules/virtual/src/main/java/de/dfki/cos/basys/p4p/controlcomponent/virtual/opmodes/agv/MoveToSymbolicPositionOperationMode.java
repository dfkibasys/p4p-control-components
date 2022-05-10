package de.dfki.cos.basys.p4p.controlcomponent.virtual.opmodes.agv;

import de.dfki.cos.basys.common.rest.mir.MirService;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.virtual.service.agv.AgvService;
import de.dfki.cos.basys.p4p.controlcomponent.virtual.service.agv.Unit;
import de.dfki.cos.mrk40.avro.*;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@OperationMode(name = "MoveSymbolic", shortName = "MVSYM", description = "moves AGV to a symbolic position",
		allowedCommands = {	ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP },
		allowedModes = { ExecutionMode.SIMULATE })
public class MoveToSymbolicPositionOperationMode extends BaseOperationMode<AgvService> {

	@Parameter(name = "position", direction = ParameterDirection.IN)
	private String position = "_HOME_";

	public MoveToSymbolicPositionOperationMode(BaseControlComponent<AgvService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		getService(AgvService.class).sendMessage("MoveToSymbolicPositionOperationMode.onResetting - start");
		getService(AgvService.class).setUnitState(Unit.State.IDLE);
		getService(AgvService.class).sendMessage("MoveToSymbolicPositionOperationMode.onResetting - finished");
	}

	@Override
	public void onStarting() {
		getService(AgvService.class).sendMessage("MoveToSymbolicPositionOperationMode.onStarting - start");
		getService(AgvService.class).gotoKnownPose(position);
		getService(AgvService.class).sendMessage("MoveToSymbolicPositionOperationMode.onStarting - finished");
	}

	@Override
	public void onExecute() {
		getService(AgvService.class).sendMessage("MoveToSymbolicPositionOperationMode.onExecute - start");

		try {
			boolean executing = true;
			while(executing) {
				var state = getService(AgvService.class).getUnitState();
				LOGGER.debug("UnitState is " + state);

				switch (state) {
					case PENDING:
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
						break;
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			component.setErrorStatus(3, e.getMessage());
			component.stop(component.getOccupierId());
		}

//		Pose3dStamped pose = getService(AgvService.class).getCurrentPose();
//
//
//		float step = 0.02f;
//
//		while (true)
//		{
//			if (pose.getPose().getPosition().getX() > 9.060)
//			{
//				pose.getPose().getPosition().setX(pose.getPose().getPosition().getX() - step);
//				pose.getPose().getOrientation().setY(1);
//			}
//			else if (pose.getPose().getPosition().getZ()> -0.873)
//			{
//				pose.getPose().getPosition().setZ(pose.getPose().getPosition().getZ() - step);
//				pose.getPose().getOrientation().setY(0);
//			}
//			else
//			{
//				break;
//			}
//
//			Instant now = Instant.now();
//			pose.getTimestamp().setSeconds(now.getEpochSecond());
//			pose.getTimestamp().setNseconds(now.getNano());
//
//			getService(AgvService.class).sendPose(pose);
//
//			try {
//				TimeUnit.MILLISECONDS.sleep(20);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}

		getService(AgvService.class).sendMessage("MoveToSymbolicPositionOperationMode.onExecute - finished");

	}

	@Override
	public void onCompleting() {
		getService(AgvService.class).sendMessage("MoveToSymbolicPositionOperationMode.onCompleting");
	}

	@Override
	public void onStopping() {
		getService(AgvService.class).sendMessage("MoveToSymbolicPositionOperationMode.onResetting");
	}
}
