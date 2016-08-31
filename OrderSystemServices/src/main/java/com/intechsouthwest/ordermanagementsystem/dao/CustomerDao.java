package com.intechsouthwest.ordermanagementsystem.dao;

import org.springframework.data.repository.CrudRepository;
import com.intechsouthwest.ordermanagementsystem.domain.Customer;

/**
 * Created by gregorylaflash on 7/31/16.
 */
public interface CustomerDao extends CrudRepository<Customer, String> {
    Customer findByName(String name);
}
