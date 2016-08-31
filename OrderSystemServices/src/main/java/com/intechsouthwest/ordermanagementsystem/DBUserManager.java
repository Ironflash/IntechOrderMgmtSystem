package com.intechsouthwest.ordermanagementsystem;

import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import com.intechsouthwest.ordermanagementsystem.dao.UserDao;
import com.intechsouthwest.ordermanagementsystem.domain.User;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gregorylaflash on 8/23/16.
 */
@Component
public class DBUserManager extends UserEntityManager {

    @Autowired
    private UserDao userDao;

    @Override
    public UserEntity findUserById(String username) {
        Assert.notNull(username, "username must not be null");
        //TODO
        User user = userDao.findByName(username);  //getUserService().findUserByusername(username);
        return toActivitiUser(user);
    }

    @Override
    public List<org.activiti.engine.identity.User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
        return Arrays.asList(findUserById(query.getId()));
    }

    private UserEntity toActivitiUser(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(user.getEmail());
        userEntity.setFirstName(user.getName());
        userEntity.setId(String.valueOf(user.getId()));

        return userEntity;
    }

    @Override
    public Boolean checkPassword(String userId, String password) {
        return true;
    }


}
