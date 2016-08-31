package com.intechsouthwest.ordermanagementsystem.dao;

import org.springframework.data.repository.CrudRepository;
import com.intechsouthwest.ordermanagementsystem.domain.PurchaseOrder;

/**
 * Created by gregorylaflash on 7/31/16.
 */
public interface PurchaseOrderDao extends CrudRepository<PurchaseOrder, String> {
    PurchaseOrder findByOrderID(String orderID);
}
