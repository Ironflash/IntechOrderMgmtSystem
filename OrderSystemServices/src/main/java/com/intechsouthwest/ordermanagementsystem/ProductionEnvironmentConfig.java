package com.intechsouthwest.ordermanagementsystem;

import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by gregorylaflash on 8/24/16.
 */
@Configuration
@Profile("production")
public class ProductionEnvironmentConfig {

    @Bean
    public UserEntityManager customUserManager() {
        return new CustomUserManager2();
    }
}
