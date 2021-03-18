package de.dfki.cos.basys.p4p.controlcomponent.mir.test;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;

public class JerseyClientTestServiceProvider implements ServiceProvider<JerseyClientTestService>, JerseyClientTestService {


	protected final Logger LOGGER = LoggerFactory.getLogger(JerseyClientTestServiceProvider.class.getName());

	private final String rootSegment = "/api/v1";
	protected Client client = ClientBuilder.newClient();
	protected WebTarget endpoint = null;
	

	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		this.endpoint = client.target(connectionString).path(rootSegment);	
		return true;
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return endpoint != null;
	}

	@Override
	public JerseyClientTestService getService() {
		return this;
	}


	@Override
	public void test() {
		List<AASDescriptor> result = endpoint.path("/registry")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get(new GenericType<List<AASDescriptor>>() {});
		LOGGER.debug("****************************************************");	
		for (AASDescriptor aasDescriptor : result) {
			LOGGER.debug(aasDescriptor.toString());	
		}
		LOGGER.debug("****************************************************");

	}
	
}
