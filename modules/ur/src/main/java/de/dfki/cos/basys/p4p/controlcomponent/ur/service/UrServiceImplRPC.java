package de.dfki.cos.basys.p4p.controlcomponent.ur.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.URState.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.URState.WorkState;

public class UrServiceImplRPC implements UrService, ServiceProvider<UrService>{

	private XmlRpcClient client;
	private MissionState missionState = MissionState.PENDING;
	
	private final Logger LOGGER;

	private Properties config;	
	private boolean connected = false;
	
	public UrServiceImplRPC(Properties config) {
		this.config = config;
		LOGGER =  LoggerFactory.getLogger("URServiceImplRPC");
	}

	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		client = new XmlRpcClient();
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL(connectionString));
            client.setConfig(config);
            connected = true;
        } catch (MalformedURLException ex) {
           LOGGER.error("Exception occurred: {}", ex.toString());
           connected = false;
        }

		return connected;
	}

	@Override
	public void disconnect() {
		connected = false;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public UrService getService() {
		return this;
	}

	@Override
	public void moveToSymbolicPosition(String positionName) {
		Integer ret = URRPCConstants.KNOWN_POSITIONS.get(positionName);
		if(ret == null) {
			missionState = MissionState.REJECTED;
		}
		else {
			setCurrentRoutine(ret.intValue());		
			missionState = MissionState.ACCEPTED;		
		}	
	}

	@Override
	public void pickAndPlaceSymbolic(String objectType, String sourceLocation, String targetLocation) {
		throw new NotImplementedException("TODO");
	}

	@Override
	public void pickSymbolic(String objectType, String sourceLocation) {
		throw new NotImplementedException("TODO");
	}
	@Override
	public void placeSymbolic(String objectType, String targetLocation) {
		throw new NotImplementedException("TODO");
	}
	@Override
	public void joinSymbolic(String objectTypeA, String objectTypeB) {
		throw new NotImplementedException("TODO");
	}

	@Override
	public MissionState getMissionState() {
		// Infer mission state via workstate . . .
		WorkState wState = getWorkState();
		switch(wState) {
		case BUSY:
			missionState = MissionState.EXECUTING;
			break;
		case FINISHED:
			missionState = MissionState.DONE;
			break;
		}
		return missionState;
	}

	@Override
	public WorkState getWorkState() {
		String wstate = getCurrentStatus().toString();
		WorkState state = null;
		switch(wstate) {
			case "busy":
				state = WorkState.BUSY;
				break;
			case "finished":
				state = WorkState.FINISHED;
				break;
			default:				
		}
		return state;
	}
	
	@Override
	public String getStatus() {
		return getCurrentStatus().toString();
	}
	

	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}
	
	/*
	 * private XML-RPC functions 
	 */
	

	private Object getCurrentRoutine() {
		Object params[] = {};
		try {
			return client.execute("get_routine", params);
		} catch (XmlRpcException ex) {
			LOGGER.error("Exception occurred: {}", ex.toString());
			return null;
		}
	}

	private void setCurrentRoutine(int code) {
		Object params[] = { code };
		try {
			client.execute("set_routine", params);
		} catch (XmlRpcException ex) {
			LOGGER.error("Exception occurred: {}", ex.toString());
		}
	}

	private Object getCurrentStatus() {
		Object params[] = {};
		try {
			return client.execute("get_status", params);
		} catch (XmlRpcException ex) {
			LOGGER.error("Exception occurred: {}", ex.toString());
			return null;
		}
	}

}
