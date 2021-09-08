package com.zx.workflow.comon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.task.DelegationState;

import java.io.Serializable;
import java.util.Date;

/**
 * Auto-generated: 2019-09-02 11:55:26
 *
 * @author liuxz
 * @date 2019.09.01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String owner;
    private Integer assigneeUpdatedCount;
    private String originalAssignee;
    private String assignee;
    private String parentTaskId;
    private String name;
    private String localizedName;
    private String description;
    private String localizedDescription;
    private Integer priority;
    private Date createTime;
    private Date dueDate;
    private Integer suspensionState;
    private String category;
    private Boolean isIdentityLinksInitialized;
    private String executionId;
    private String processInstanceId;
    private String processDefinitionId;
    private String taskDefinitionId;
    private String scopeId;
    private String subScopeId;
    private String scopeType;
    private String scopeDefinitionId;
    private String taskDefinitionKey;
//    private String formKey;
    private Boolean isCanceled;
    private Boolean isCountEnabled;
    private Integer variableCount;
    private Integer identityLinkCount;
    private Integer subTaskCount;
    private Date claimTime;
    private String tenantId;
    private String eventName;
    private String eventHandlerId;
    private String idPrefix;
    private Boolean forcedUpdate;
    private Boolean isInserted;
    private Boolean isUpdated;
    private Boolean isDeleted;

    private Object variable;





//    String getId();
//
//    String getName();
//
//    void setName(String var1);

//    String getDescription();
//
//    void setDescription(String var1);
//
//    int getPriority();
//
//    void setPriority(int var1);
//
//    String getOwner();

//    void setOwner(String var1);
//
//    String getAssignee();
//
//    void setAssignee(String var1);
//
//    DelegationState getDelegationState();
//
//    void setDelegationState(DelegationState var1);
//
//    String getProcessInstanceId();
//
//    String getExecutionId();
//
//    String getProcessDefinitionId();

//    String getCaseInstanceId();
//
//    void setCaseInstanceId(String var1);
//
//    String getCaseExecutionId();
//
//    String getCaseDefinitionId();
//
//    Date getCreateTime();

//    String getTaskDefinitionKey();
//
//    Date getDueDate();
//
//    void setDueDate(Date var1);

//    Date getFollowUpDate();
//
//    void setFollowUpDate(Date var1);
//
//    void delegate(String var1);
//
//    void setParentTaskId(String var1);
//
//    String getParentTaskId();
//
//    boolean isSuspended();
//
//    String getFormKey();

//    String getTenantId();
//
//    void setTenantId(String var1);

}