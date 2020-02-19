package de.dfki.cos.basys.p4p.controlcomponent.smartwatch;

import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.thrift.TException;
import org.slf4j.Logger;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.hrc.mq3t.rpc.client.impl.RPCClientSimple;
import de.dfki.cos.hrc.mq3t.rpc.util.RPCFactory;
import de.dfki.iui.hrc.hybritcommand.CommandState;
import de.dfki.iui.hrc.hybritcommand.HumanTaskDTO;
import de.dfki.iui.smartwatch.Smartwatch;

public class NotificationServiceImpl implements NotificationService, ServiceProvider<NotificationService>{
	private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);
	private static final String CONNECTION_STRING_PATTERN = "mq3t.rpc:\\/\\/(?<host>.*):(?<port>\\d*)";
	
	private RPCClientSimple<Smartwatch.Client> client = null;
	private String connectionString = "";
	private ComponentContext cc = null;

	public NotificationServiceImpl(Properties config) {
		
	}
	
	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		this.connectionString = connectionString;
		cc = context;
		if(client == null) {
			Pattern pattern = Pattern.compile(CONNECTION_STRING_PATTERN);
			Matcher matcher = pattern.matcher(connectionString);
			if(matcher.matches()) {
				client = RPCFactory.createRPCClientSimple(
						Smartwatch.Client.class,
						matcher.group("host"),
						Integer.parseInt(matcher.group("port")));
			}
			else {
				LOG.error("Invalid or malformed service connection string! {} does not match "
						+ "the expected pattern {}! Connection failed! ",
						connectionString, CONNECTION_STRING_PATTERN);
				return false;
			}
		}
		else if(client.isConnected()) {
			LOG.warn("Client already connected! Consider disconnecting first.");
			return true;
		}
		client.connect();
		return client.isConnected();
	}

	@Override
	public void disconnect() {
		if(client == null) {
			LOG.error("Client is null! Disconnecting aborted! You have to connect first!");
			return;
		}
		client.disconnect();
	}

	@Override
	public boolean isConnected() {
		if(client == null) {
			return false;
		}
		return client.isConnected();
	}
	
	@Override
	public void reconnect() {
		disconnect();
		while(!connect(cc, connectionString)) {
			LOG.warn("Reconnecting failed! Retrying ... ");
			sleep(1000);			
		}
		LOG.debug("Reconnected succesfully!");
	}

	@Override
	public NotificationService getService() {
		return this;
	}

	@Override
	public CommandState requestTaskExecution(HumanTaskDTO task) throws TException {
		return client.RPC.requestTaskExecution(task);
	}

	@Override
	public CommandState displayInfoMessage(String message) throws TException {
		return client.RPC.showNotification(message);
	}

	@Override
	public CommandState getCommandState(String taskId) throws TException{
		return client.RPC.getCommandState(taskId).state;
	}
	
	public void sleep(long ms) {
		try {
			TimeUnit.MILLISECONDS.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
