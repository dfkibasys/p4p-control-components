package de.dfki.cos.basys.p4p.controlcomponent.mir;

import java.util.Properties;
import java.util.function.Supplier;

import de.dfki.cos.basys.common.component.impl.ServiceManagerImpl;
import de.dfki.cos.basys.common.mirrestclient.MirService;
import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

public class MirControlComponent extends BaseControlComponent {

	public MirControlComponent(Properties config) {
		super(config);
		serviceManager = new ServiceManagerImpl<MirService>(config, new Supplier<MirServiceImpl>() {
			@Override
			public MirServiceImpl get() {
				MirServiceImpl service = new MirServiceImpl(config);				
				return service;
			}
		});
	}
	
	@Override
	protected void registerOperationModes() {		
		OperationMode opMode = new MoveToSymbolicPositionOperationMode(this);
		registerOperationMode(opMode);		
	}
	
}
