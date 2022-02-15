package de.dfki.cos.basys.p4p.controlcomponent.baxter;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.baxter.opmodes.MoveToSymbolicPositionOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.baxter.service.BaxterService;

public class BaxterPlatformControlComponent extends BaseControlComponent<BaxterService> {

	public BaxterPlatformControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		OperationMode opMode = new MoveToSymbolicPositionOperationMode(this);
		registerOperationMode(opMode);		
	}
	
}
