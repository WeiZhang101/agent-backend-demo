package org.tw.agent_backend_demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tw.agent_backend_demo.models.enums.VisibilityType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisibilityScope {
    private VisibilityType type;
    private List<String> values;
} 