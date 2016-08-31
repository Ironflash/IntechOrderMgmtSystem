package com.intechsouthwest.ordermanagementsystem;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by gregorylaflash on 8/23/16.
 */
@Service
class CustomUserManagerFactory implements SessionFactory {
    @Autowired
    UserEntityManager customUserManager;

    public Class<?> getSessionType() {
        return UserIdentityManager.class;
    }

    public Session openSession() {
        return customUserManager;
    }
}
