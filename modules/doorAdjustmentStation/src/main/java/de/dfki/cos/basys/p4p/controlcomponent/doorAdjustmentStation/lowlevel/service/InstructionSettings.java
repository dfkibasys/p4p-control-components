package de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.p4p.controlcomponent.doorAdjustmentStation.lowlevel.dto.ShowInstructionRequest;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public class InstructionSettings {

    private static final InstructionSettings INSTANCE = new InstructionSettings();

    private final Map<String, ShowInstructionRequest> requests;

    private InstructionSettings() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, ShowInstructionRequest> loaded = Collections.emptyMap();

        try (InputStream in = ShowInstructionRequest.class.getClassLoader().getResourceAsStream("instructions.json")) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: instructions.json");
            }
            loaded = objectMapper.readValue(in, new TypeReference<Map<String, ShowInstructionRequest>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load instructions.json", e);
        }

        this.requests = loaded;
    }

    public static InstructionSettings getInstance() {
        return INSTANCE;
    }

    public ShowInstructionRequest getInstruction(String taskId) {
        return requests.get(taskId);
    }
}
