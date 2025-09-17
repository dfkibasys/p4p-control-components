package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StateConfidence {
    private String state;
    private Float confidence;
}