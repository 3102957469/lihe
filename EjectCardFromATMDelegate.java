package com.zx.workflow.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-camunda
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 08/01/20
 * Time: 06.09
 */
@Description(value = "Delegate responsible for ejecting card from ATM.")
public class EjectCardFromATMDelegate implements JavaDelegate {
    private Logger logger = LoggerFactory.getLogger(EjectCardFromATMDelegate.class);

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        logger.info("----Ejecting card from ATM...");
        logger.info("-------Ending ATM transaction process...");
    }
}
