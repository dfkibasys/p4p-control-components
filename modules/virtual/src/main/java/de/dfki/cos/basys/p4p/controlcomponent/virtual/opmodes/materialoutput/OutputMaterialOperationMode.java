package de.dfki.cos.basys.p4p.controlcomponent.virtual.opmodes.materialoutput;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.virtual.service.materialoutput.MaterialOutputService;

import java.util.concurrent.TimeUnit;

@OperationMode(name = "OutputMaterial", shortName = "OUTPUTMT", description = "Outputs material",
        allowedCommands = { ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP },
        allowedModes = { ExecutionMode.SIMULATE })
public class OutputMaterialOperationMode extends BaseOperationMode<MaterialOutputService> {

    public OutputMaterialOperationMode(BaseControlComponent<MaterialOutputService> component) {
        super(component);
    }

    @Override
    public void onResetting() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStarting() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExecute() {
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleting() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStopping() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
