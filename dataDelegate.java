package com.zx.workflow.delegate;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-camunda
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 08/01/20
 * Time: 06.08
 */
@Description(value = "Delegate for validating PIN.")
@Service
public class dataDelegate implements JavaDelegate {
    private final Logger logger = LoggerFactory.getLogger(dataDelegate.class);


    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
//        Long pinCode = (Long) delegateExecution.getVariable("pin_code");
//        if (pinCode == null) {
//            logger.error("PIN code is not provided");
//            throw new Exception("Pin code is not provided!");
//        }
//

        // set valid PIN variable.
        delegateExecution.setVariable("keyi", true);
    }
}
