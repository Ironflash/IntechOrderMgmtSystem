package com.intechsouthwest.ordermanagementsystem;

import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gregorylaflash on 8/24/16.
 */
@Configuration
@Profile("default")
//@AutoConfigureBefore(org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class)
public class ActivitiConfiguration implements ProcessEngineConfigurationConfigurer {

//    @Resource(name = "myGroupManagerFactory")
//    private SessionFactory groupManagerFactory;

    @Autowired
    private CustomUserManagerFactory userManagerFactory;

    public void configure(SpringProcessEngineConfiguration pec) {
        pec.setDbIdentityUsed(false);

        List<SessionFactory> customSessionFactories = new ArrayList<SessionFactory>();
        customSessionFactories.add(userManagerFactory);
//        customSessionFactories.add(userIdentityManagerFactory);
//        customSessionFactories.add(groupManagerFactory);
        if (pec.getCustomSessionFactories() == null){
            pec.setCustomSessionFactories(customSessionFactories);
        }
        else{
            pec.getCustomSessionFactories().addAll(customSessionFactories);
        }
    }
}
