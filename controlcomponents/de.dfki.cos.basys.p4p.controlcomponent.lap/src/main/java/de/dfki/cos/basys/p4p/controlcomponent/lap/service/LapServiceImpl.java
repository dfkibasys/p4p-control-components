package de.dfki.cos.basys.p4p.controlcomponent.lap.service;

import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;

public class LapServiceImpl implements LapService, ServiceProvider<LapService> {

	protected final Logger LOGGER = LoggerFactory.getLogger(LapServiceImpl.class.getName());
	private boolean connected = false;
	private String connectionString = "http://10.2.0.47:9000/laserControl";
	private String missionState = "pending";
	
	public LapServiceImpl(Properties config) {

	}
	
	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		
		//TODO: Test if connectionString works

		connected = true;
		
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
	public LapService getService() {
		return this;
	}

	@Override
	public void projectRectangle(double x, double y, double z, double width, double height, int color) {
		
		missionState = "executing";
		
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(connectionString);
		
		JsonObject obj = 
				Json.createObjectBuilder()
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("width", width)
				.add("height", height)
				.add("color", color)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request().put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == 200) {
			LOGGER.debug("success");
			missionState = "done";
		}
		
	}
	

	@Override
	public void reset() {
		missionState = "pending";
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getMissionState() {
		return missionState;
	}

	@Override
	public String getWorkState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
