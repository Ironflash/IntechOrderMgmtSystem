package com.intechsouthwest.ordermanagementsystem;

import com.intechsouthwest.ordermanagementsystem.dao.UserDao;
import com.intechsouthwest.ordermanagementsystem.domain.User;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by gregorylaflash on 8/23/16.
 */
@Component
public class CustomUserManager2 extends UserEntityManager {

    @Autowired
    private UserDao userDao;

    @Override
    public UserEntity findUserById(String username) {
        Assert.notNull(username, "username must not be null");
        //TODO
        User user = userDao.findByEmail(username);  //getUserService().findUserByusername(username);
        return toActivitiUser(user);
    }

    @Override
    public List<org.activiti.engine.identity.User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
        return super.findUserByQueryCriteria(query, page);
    }

    private UserEntity toActivitiUser(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(user.getEmail());
        userEntity.setFirstName(user.getName());
        userEntity.setId(String.valueOf(user.getId()));

        return userEntity;
    }

}
