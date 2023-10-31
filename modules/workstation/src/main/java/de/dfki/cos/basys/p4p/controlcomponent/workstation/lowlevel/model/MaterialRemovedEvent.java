package de.dfki.cos.basys.p4p.controlcomponent.workstation.lowlevel.model;

import lombok.Data;

@Data
public class MaterialRemovedEvent {
    String material;
    int instock;
    int removed;
}
