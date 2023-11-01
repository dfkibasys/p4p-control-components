package de.dfki.cos.basys.p4p.controlcomponent.workstation.highlevel.opmodes;

import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.camunda.opmodes.CamundaOperationMode;
import de.dfki.cos.basys.controlcomponent.camunda.service.CamundaService;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;

@OperationMode(name = "PickAndPlaceSymbolic", shortName = "PP_SYM", description = "Pick and place an object of specified type from its current location" +
        "to a symbolic target location")
public class PickAndPlaceSymbolicOperationMode extends CamundaOperationMode {

    public PickAndPlaceSymbolicOperationMode(BaseControlComponent<CamundaService> component){
        super(component);
    }

    // e.g. ?material: nuts | ?quantity: 4 | ?target_location: assembly_mount | ?workstep_id: boxed_microcontroller_step_001
    // TODO later: Find ?mat_location of ?material with the help of Knowledge Graph
    // e.g. 4 nuts are available in ?mat_location: box_01

    // Execute WGS / PICK / PLACE BPMN process enhanced with ?mat_location, ?quantity (PICK) and ?material, ?quantity, ?target_location (PLACE)

    @Parameter(name = "pps_material", direction = ParameterDirection.IN)
    private String pps_material = "";
    @Parameter(name = "pps_quantity", direction = ParameterDirection.IN)
    private long pps_quantity = 1;
    @Parameter(name = "pps_mat_location", direction = ParameterDirection.IN)
    private String pps_mat_location = "";
    @Parameter(name = "pps_workstep_id", direction = ParameterDirection.IN)
    private String pps_workstep_id = "";

}
