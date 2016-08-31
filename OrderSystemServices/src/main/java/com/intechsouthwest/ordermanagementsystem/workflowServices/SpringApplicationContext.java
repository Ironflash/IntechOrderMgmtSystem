package com.intechsouthwest.ordermanagementsystem.workflowServices;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by gregorylaflash on 8/20/16.
 */
@Component
public class SpringApplicationContext implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.CONTEXT = applicationContext;
    }

    public static ApplicationContext getContext(){
        return CONTEXT;
    }
}
