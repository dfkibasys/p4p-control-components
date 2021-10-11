package de.dfki.cos.basys.p4p.platform.bdevws.analysisservice.impl;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.nextbreakpoint.flinkclient.api.ApiException;
import com.nextbreakpoint.flinkclient.api.FlinkApi;
import com.nextbreakpoint.flinkclient.model.JarFileInfo;
import com.nextbreakpoint.flinkclient.model.JarListInfo;
import com.nextbreakpoint.flinkclient.model.JarUploadResponseBody;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.platform.runtime.processcontrol.deployment.DeploymentService;

public class FlinkDeploymentService implements ServiceProvider<DeploymentService>, DeploymentService {

	private FlinkApi api = null;
	
	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		api = new FlinkApi();
		api.getApiClient().setBasePath(connectionString);
        api.getApiClient().getHttpClient().setConnectTimeout(20000, TimeUnit.MILLISECONDS);
        api.getApiClient().getHttpClient().setWriteTimeout(30000, TimeUnit.MILLISECONDS);
        api.getApiClient().getHttpClient().setReadTimeout(30000, TimeUnit.MILLISECONDS);

        return isConnected();
	}

	@Override
	public void disconnect() {
		api = null;		
	}

	@Override
	public boolean isConnected() {
		return api!= null;
	}

	@Override
	public DeploymentService getService() {		
		return this;
	}
	
	@Override
	public String createDeployment(String filePath) {
		try {			
			///tmp/flink-web-523a64c6-a4fc-4125-8661-a95c84c88df3/flink-web-upload/938dc0f9-0a79-4014-bcc2-059be8dbd937_curator-client-5.1.0.jar
			JarUploadResponseBody response = api.uploadJar(new File(filePath));
			String tmp = response.getFilename();
			
			File f = new File(response.getFilename());
			String id = f.getName();
			return id;
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void deleteDeployment(String id) {
		try {
			api.deleteJar(id);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void clearDeployments() {
		try {
			JarListInfo jars = api.listJars();
			for (JarFileInfo jar : jars.getFiles()) {
				deleteDeployment(jar.getId());
			}
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



}
