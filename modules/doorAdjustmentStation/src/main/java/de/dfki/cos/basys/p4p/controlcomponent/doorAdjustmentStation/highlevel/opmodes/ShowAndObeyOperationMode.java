package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.highlevel.opmodes;

import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.camunda.opmodes.CamundaOperationMode;
import de.dfki.cos.basys.controlcomponent.camunda.service.CamundaService;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

@OperationMode(name = "ShowAndObey", shortName = "SAO", description = "Show and obey the instruction")
public class ShowAndObeyOperationMode extends CamundaOperationMode {

    public ShowAndObeyOperationMode(BaseControlComponent<CamundaService> component){
        super(component);
    }

    // Execute SHOW / EXECUTE BPMN process
    @Parameter(name = "sao_task_id", direction = ParameterDirection.IN)
    private String sao_task_id = "";

}
