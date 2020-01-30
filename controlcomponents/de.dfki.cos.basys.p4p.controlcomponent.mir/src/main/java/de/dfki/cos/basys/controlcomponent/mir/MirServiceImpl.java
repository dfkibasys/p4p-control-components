package de.dfki.cos.basys.controlcomponent.mir;

import java.util.List;
import java.util.Properties;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.common.mirrestclient.MiRState;
import de.dfki.cos.basys.common.mirrestclient.MirRestService;
import de.dfki.cos.basys.common.mirrestclient.MirService;
import de.dfki.cos.basys.common.mirrestclient.dto.MissionDefinition;
import de.dfki.cos.basys.common.mirrestclient.dto.MissionInstance;
import de.dfki.cos.basys.common.mirrestclient.dto.MissionInstanceInfo;
import de.dfki.cos.basys.common.mirrestclient.dto.MissionOrder;
import de.dfki.cos.basys.common.mirrestclient.dto.Status;
import de.dfki.cos.basys.common.mirrestclient.dto.SymbolicPosition;
import de.dfki.cos.basys.common.mirrestclient.dto.SymbolicPositionInfo;

public class MirServiceImpl implements MirService, ServiceProvider<MirService> {

	private MirRestService service;
	private String auth = null;
	
	public MirServiceImpl(Properties config) {
		auth = config.getProperty("auth");
	}
	
	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		service = new MirRestService(connectionString, auth);		
		return isConnected();
	}

	@Override
	public void disconnect() {
		service = null;
	}

	@Override
	public boolean isConnected() {
		//TODO check also for mission definitions
		return (service != null);
	}

	@Override
	public MirService getService() {
		return this;
	}
	
	@Override
	public Status getRobotStatus() {
		return service.getRobotStatus();
	}

	@Override
	public Status setRobotStatus(MiRState state) {
		return service.setRobotStatus(state);
	}

	@Override
	public List<MissionDefinition> getMissionDefinitions() {
		return service.getMissionDefinitions();
	}

	@Override
	public List<MissionDefinition> getMissionDefinitionsInArea(String areaId) {
		return service.getMissionDefinitionsInArea(areaId);
	}

	@Override
	public List<MissionInstance> getAllMissionInstancesInQueue() {
		return service.getAllMissionInstancesInQueue();
	}

	@Override
	public MissionInstanceInfo getMissionInstanceInfo(int missionInstanceId) {
		return service.getMissionInstanceInfo(missionInstanceId);
	}

	@Override
	public MissionInstanceInfo enqueueMissionInstance(MissionOrder order) {
		return service.enqueueMissionInstance(order);
	}

	@Override
	public boolean dequeueMissionInstance(int missionInstanceId) {
		return service.dequeueMissionInstance(missionInstanceId);
	}

	@Override
	public List<SymbolicPosition> getPositions() {
		return service.getPositions();
	}

	@Override
	public List<SymbolicPosition> getMapPositions(String mapId) {
		return service.getMapPositions(mapId);
	}

	@Override
	public SymbolicPositionInfo getPositionInfo(String positionId) {
		return service.getPositionInfo(positionId);
	}

	@Override
	public List<MissionInstance> getMissionInstancesInQueue() {
		return service.getMissionInstancesInQueue();
	}

	@Override
	public MissionInstanceInfo enqueueMissionInstance(String missionDefinitionId) {
		return service.enqueueMissionInstance(missionDefinitionId);
	}

	@Override
	public MissionInstanceInfo enqueueMissionInstanceByName(String missionDefinitionName) {
		return service.enqueueMissionInstanceByName(missionDefinitionName);
	}

	@Override
	public MissionInstanceInfo gotoSymbolicPosition(String positionName) {
		return service.gotoSymbolicPosition(positionName);
	}

	@Override
	public MissionInstanceInfo gotoAbsolutePosition(float posX, float posY, float orientation) {
		return service.gotoAbsolutePosition(posX, posY, orientation);
	}

	@Override
	public List<SymbolicPosition> getCurrentMapPositions() {
		return service.getCurrentMapPositions();
	}

	@Override
	public boolean isQueueEmpty() {
		return service.isQueueEmpty();
	}

	@Override
	public boolean isMissionInQueue(String missionDefinitionId) {
		return service.isMissionInQueue(missionDefinitionId);
	}

}
