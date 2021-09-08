package com.zx.workflow.comon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String processId;
    private String processKey;
    private String taskId;
    private Map<String,Object> variable;
    private String userId;
    private String ramark1;
    private String ramark2;
    private String ramark3;
}