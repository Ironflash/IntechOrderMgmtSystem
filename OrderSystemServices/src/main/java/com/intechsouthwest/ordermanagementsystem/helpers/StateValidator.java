package com.intechsouthwest.ordermanagementsystem.helpers;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.intechsouthwest.ordermanagementsystem.dao.CustomerDao;
import com.intechsouthwest.ordermanagementsystem.dao.PurchaseOrderDao;
import com.intechsouthwest.ordermanagementsystem.domain.PurchaseOrder;

/**
 * Created by gregorylaflash on 8/17/16.
 */
@Component
public class StateValidator {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private PurchaseOrderDao purchaseOrderDao;
    @Autowired
    private CustomerDao customerDao;


    private StateValidator() {
    }


    public StateValidator customerExists(String customerName) {

        if(customerName == null)
            throw new HelperException("Customer name cannot be null");
        else if(customerName.isEmpty())
            throw new HelperException("Customer name cannot be empty");
        else if(!customerDao.exists(customerName))
            throw new HelperException(String.format("Customer %s not found",customerName));

        return this;
    }

    public StateValidator customerAccountNotFrozen(String customerName) {
        if(customerDao.findByName(customerName).getFrozen())
            throw new HelperException("Customer account is frozen");

        return this;
    }

    public StateValidator purchaseOrderExists(String purchaseOrderID) {
        if(purchaseOrderID == null)
            throw new HelperException("Purchase order ID cannot be null");
        else if(purchaseOrderID.isEmpty())
            throw new HelperException("Purchase order ID cannot be empty");
        else if(!purchaseOrderDao.exists(purchaseOrderID))
            throw new HelperException(String.format("Purchase order not found with ID %s",purchaseOrderID));

        return this;
    }

    public StateValidator purchaseOrderHasWF(String orderID) {
        PurchaseOrder po = purchaseOrderDao.findByOrderID(orderID);

        if(po.getWfInstanceID() == null || po.getWfInstanceID().isEmpty())
            throw new HelperException("Purchase Order does not have an active workflow");
        else{
            if( runtimeService.createProcessInstanceQuery().active().processInstanceId(po.getWfInstanceID()).count() == 0)
                throw new HelperException("Workflow is not active for purchase order");
        }

        return this;
    }

}
