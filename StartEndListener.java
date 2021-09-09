package com.zx.workflow.comon;

import com.zx.workflow.delegate.dataDelegate;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class StartEndListener extends BaseWorkflowListener{
    private final Logger logger = LoggerFactory.getLogger(dataDelegate.class);
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String eventName = execution.getEventName();
        listenerLogic(eventName);


    }

    @Override
    public void notify(DelegateTask delegateTask) {
        logger.info("------123");
        if(delegateTask.getProcessInstanceId() !=null) {
            try {
                Task tasks = taskService.createTaskQuery()
                        .processInstanceId(delegateTask.getProcessInstanceId())
                        .singleResult();
                logger.info(tasks.getId() + "---------------dataDelegate execute " +
                        delegateTask.getProcessInstanceId() + "-----" +
                        delegateTask.getEventName());
                taskService.complete(tasks.getId());
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}


abstract class BaseWorkflowListener implements IWorkflowListener{
    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String eventName = execution.getEventName();
        listenerLogic(eventName);
    }
    @Override
    public void notify(DelegateTask delegateTask) {
    }
    protected void listenerLogic(String eventName) {
        if("create".equals(eventName)){
            System.out.println("create===========流程启动");
        }else if("assigment".equals(eventName)){
            System.out.println("assigment===========流程部署");
        }else if("complete".equals(eventName)){
            System.out.println("complete===========流程完成");
        }else if("delete".equals(eventName)){
            System.out.println("delete===========流程结束");
        }else if("start".equals(eventName)){
            System.out.println("start===========流程启动");
        }else if("end".equals(eventName)){
            System.out.println("end===========流程结束");
        }else if("take".equals(eventName)) {
            System.out.println("take===========流程执行");
        }
    }
}

/**
 * 监听器
 */
interface IWorkflowListener extends TaskListener, ExecutionListener {
}

