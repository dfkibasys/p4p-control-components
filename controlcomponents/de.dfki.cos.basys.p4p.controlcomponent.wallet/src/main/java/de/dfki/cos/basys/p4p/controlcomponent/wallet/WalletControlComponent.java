package de.dfki.cos.basys.p4p.controlcomponent.wallet;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

public class WalletControlComponent extends BaseControlComponent<WalletService> {

	public WalletControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		registerOperationMode(new MoveToSymbolicPositionOperationMode(this));
		registerOperationMode(new LiftOperationMode(this));
	}
	
}
