package com.intechsouthwest.ordermanagementsystem.helpers;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intechsouthwest.ordermanagementsystem.helpers.WorkflowHelper.ProcessType.*;
import static com.intechsouthwest.ordermanagementsystem.helpers.WorkflowHelper.TaskType.*;

/**
 * Created by gregorylaflash on 8/17/16.
 */
@Component
@Scope("prototype")
public class WorkflowHelper {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private RepositoryService repositoryService;

    private Task task = null;
    private String wfInstanceID = null;
    private String user = null;


    public static enum ProcessType {

        PURCHASE_ORDER_SUBMISSION("purchaseOrderSubmission");

        private String type;
        ProcessType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public static enum TaskType {

        PURCHASING_REVIEW("purchasingReview", PURCHASE_ORDER_SUBMISSION),
        RENEGOTIATE_QUOTE("renegotiateQuote", PURCHASE_ORDER_SUBMISSION);

        private final String taskType;
        private final ProcessType type;

        private TaskType(String taskType, ProcessType type) {
            this.taskType = taskType;
            this.type = type;
        }

        @Override
        public String toString() {
            return taskType;
        }
    }

    public static enum TaskAction {

        REJECT_PO("REJECT_PO", PURCHASING_REVIEW),
        ORDER_ITEMS("ORDER_ITEMS", PURCHASING_REVIEW),
        OFFER_CHANGE("OFFER_CHANGE", RENEGOTIATE_QUOTE);

        private final String taskAction;
        private final TaskType type;

        private TaskAction(String taskAction, TaskType type) {
            this.taskAction = taskAction;
            this.type = type;
        }

        @Override
        public String toString() {
            return taskAction;
        }

        public TaskType getTaskType() {
            return type;
        }
    }

    public static enum CommentType {

        PURCHASING("purchasing"),
        SALES("sales");

        String type;

        private CommentType(final String type) {
            this.type = type;
        }


        @Override
        public String toString() {
            return type;
        }
    }

    private WorkflowHelper() {


    }

    public WorkflowHelper setUser(String user) {
        this.user = user;
        identityService.setAuthenticatedUserId(user);
        return this;
    }

    public WorkflowHelper setInstanceID(String wfInstanceID) {

        if(wfInstanceID == null)
            throw new HelperException("Workflow instance ID is null");

        if(wfInstanceID.isEmpty())
            throw new HelperException("Workflow instance ID is empty");

        this.wfInstanceID = wfInstanceID;

        return this;
    }

    public WorkflowHelper setTask(TaskType taskType) {

        checkWFInstanceIDSet();

        task = taskService.createTaskQuery().processInstanceId(wfInstanceID).active().taskDefinitionKey(taskType.toString()).singleResult();

        if (task == null) {
            throw new HelperException(String.format("Workflow does not have an active task of type %s",taskType));
        }

        return this;
    }



    public WorkflowHelper addComment(String comment, CommentType type) {
        checkTaskSet();

        taskService.addComment(task.getId(),wfInstanceID, type.toString(), comment);

        return this;
    }



    public WorkflowHelper completeWithAction(TaskAction action) {
        return completeWithAction(action, null);
    }

    public WorkflowHelper completeWithAction(TaskAction action, Map<String, Object> userDefinedValues) {
        checkTaskSet();
        checkActionBelongsToTask(action);

        Map<String,Object> variables = new HashMap<>();
        variables.put("action",action.toString());

        // merge in userDefinedValues
        if(userDefinedValues != null)
            userDefinedValues.entrySet().stream()
                    .filter(entry->!entry.getKey().equals("action")) // don't allow user to specify action as a variable
                    .forEach(entry -> variables.merge(entry.getKey(), entry.getValue(), (Object o, Object o2) -> o));

        taskService.complete(task.getId(), variables);

        return this;
    }

    public WorkflowHelper checkTaskIsClaimedByUser() {
        checkTaskSet();
        checkUserSet();

        if(task.getAssignee() == null){
            throw new HelperException("Task is not assigned");
        }

        if(!task.getAssignee().equals(user)){
            throw new HelperException("Task is not assigned to current user");
        }

        return this;
    }

    public void claimTask(String taskID) {
        checkUserSet();

        taskService.claim(taskID,user);
    }

    public List<String> getActiveTasks() {
        checkWFInstanceIDSet();

        return taskService.createTaskQuery().processInstanceId(wfInstanceID).active().list()
                .stream().map(task->task.getId()).collect(Collectors.toList());
    }

    private void checkUserSet() {
        if(user == null)
            throw new HelperException("User not set");
    }

    private void checkTaskSet() {
        if(task == null)
            throw new HelperException("Task not set");
    }

    private void checkWFInstanceIDSet() {
        if(wfInstanceID == null)
            throw new HelperException("Workflow instance ID not set");
    }

    private void checkActionBelongsToTask(TaskAction action) {
        checkTaskSet();
        if(!action.getTaskType().toString().equals(task.getTaskDefinitionKey()) )
            throw new HelperException(String.format("Action %s is not allowed on task %s", action, task.getTaskDefinitionKey()));
    }
}
