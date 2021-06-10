package de.dfki.cos.basys.p4p.controlcomponent.lap.service;

import java.util.List;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;

public class LapServiceImpl implements LapService, ServiceProvider<LapService> {

	protected final Logger LOGGER = LoggerFactory.getLogger(LapServiceImpl.class.getName());
	private boolean connected = false;
	private String missionState = "pending";
	WebTarget resource = null;
	
	public LapServiceImpl(Properties config) {

	}
	
	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		
		Client client = ClientBuilder.newClient();
		resource = client.target(connectionString);
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "stop")
				.build();
		
		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			connected = true;
		}
		else {
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
	public LapService getService() {
		return this;
	}

	@Override
	public void projectRectangle(double x, double y, double z, int color, double width, double height) {
		
		missionState = "executing";

		JsonObject obj = 
				Json.createObjectBuilder()
				.add("type", "rectangle")
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("color", color)
				.add("width", width)
				.add("height", height)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectString(double x, double y, double z, int color, String text, double height) {
		missionState = "executing";

		JsonObject obj = 
				Json.createObjectBuilder()
				.add("type", "string")
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("color", color)
				.add("text", text)
				.add("height", height)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectChar(double x, double y, double z, int color, String chr, double height) {
		missionState = "executing";

		JsonObject obj = 
				Json.createObjectBuilder()
				.add("type", "char")
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("color", color)
				.add("chr", chr)
				.add("height", height)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectCircle(double x, double y, double z, int color, double radius, double angleStart,
			double angleLength) {
		missionState = "executing";

		JsonObject obj = 
				Json.createObjectBuilder()
				.add("type", "circle")
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("color", color)
				.add("radius", radius)
				.add("angleStart", angleStart)
				.add("angleLength", angleLength)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectEllipse(double x, double y, double z, int color, double majorRadius, double minorRadius,
			double angleStart, double angleLength) {
		missionState = "executing";

		JsonObject obj = 
				Json.createObjectBuilder()
				.add("type", "ellipse")
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("color", color)
				.add("majorRadius", majorRadius)
				.add("minorRadius", minorRadius)
				.add("angleStart", angleStart)
				.add("angleLength", angleLength)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectLine(double x, double y, double z, int color, double x2, double y2, double z2) {
		missionState = "executing";

		JsonObject obj = 
				Json.createObjectBuilder()
				.add("type", "line")
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("color", color)
				.add("x2", x2)
				.add("y2", y2)
				.add("z2", z2)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectMovingArrows(double x, double y, double z, int color, List<Point> points, int arrowCount,
			int delay) {
		missionState = "executing";
		
		String pnts = "";
		try {
			pnts = new ObjectMapper().writeValueAsString(points);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JsonObject obj = 
				Json.createObjectBuilder()
				.add("type", "movingArrows")
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("color", color)
				.add("points", pnts)
				.add("arrowCount", arrowCount)
				.add("delay", delay)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectMovingETA(double x, double y, double z, int color, double radius, double angle, double fullTime,
			double startTime) {
		missionState = "executing";

		JsonObject obj = 
				Json.createObjectBuilder()
				.add("type", "movingETA")
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("color", color)
				.add("radius",radius)
				.add("angle", angle)
				.add("fullTime", fullTime)
				.add("startTime", startTime)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectPulsatingCircle(double x, double y, double z, int color, double innerCircleRadius,
			double middleCircleRadius, double outerCircleRadius, double angleStart, double angleLength, int delay) {
		missionState = "executing";

		JsonObject obj = 
				Json.createObjectBuilder()
				.add("type", "pulsatingCircle")
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("color", color)
				.add("innerCircleRadius",innerCircleRadius)
				.add("middleCircleRadius", middleCircleRadius)
				.add("outerCircleRadius", outerCircleRadius)
				.add("angleStart", angleStart)
				.add("angleLength", angleLength)
				.add("delay", delay)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectArrowsAndCircles(double x, double y, double z, int color, List<Point> points, int arrowCount,
			double innerCircleRadius, double middleCircleRadius, double outerCircleRadius, double angleStart,
			double angleLength, int delayArrows, int delayCircles) {
		missionState = "executing";
		
		String pnts = "";
		try {
			pnts = new ObjectMapper().writeValueAsString(points);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JsonObject obj = 
				Json.createObjectBuilder()
				.add("type", "arrowsAndCircles")
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("color", color)
				.add("points", pnts)
				.add("arrowCount", arrowCount)
				.add("innerCircleRadius", innerCircleRadius)
				.add("middleCircleRadius", middleCircleRadius)
				.add("outerCircleRadius", outerCircleRadius)
				.add("angleStart", angleStart)
				.add("angleLength", angleLength)
				.add("delayArrows", delayArrows)
				.add("delayCircles", delayCircles)
				.build();
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "projection")
				.add("data", Json.createArrayBuilder().add(obj).build())
				.build();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void stopProjection() {
		missionState = "executing";
		
		JsonObject entity = 
				Json.createObjectBuilder()
				.add("action", "stop")
				.build();
		
		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(entity, MediaType.APPLICATION_JSON));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}
	
	@Override
	public void reset() {
		missionState = "pending";
		resource = null;
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
