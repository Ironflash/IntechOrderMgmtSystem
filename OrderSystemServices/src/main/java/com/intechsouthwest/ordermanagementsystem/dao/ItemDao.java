package com.intechsouthwest.ordermanagementsystem.dao;

import org.springframework.data.repository.CrudRepository;
import com.intechsouthwest.ordermanagementsystem.domain.Item;

/**
 * Created by gregorylaflash on 8/20/16.
 */
public interface ItemDao extends CrudRepository<Item, Long>  {
}
