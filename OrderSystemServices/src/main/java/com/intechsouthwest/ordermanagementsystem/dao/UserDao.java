package com.intechsouthwest.ordermanagementsystem.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import com.intechsouthwest.ordermanagementsystem.domain.User;

/**
 * Created by gregorylaflash on 7/31/16.
 */
@Transactional
public interface UserDao extends CrudRepository<User, Long> {

    /**
     * This method will find an User instance in the database by its email.
     * Note that this method is not implemented and its working code will be
     * automagically generated from its signature by Spring Data JPA.
     */
    public User findByEmail(String email);

    public User findByName(String name);

}
