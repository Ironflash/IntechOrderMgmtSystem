package com.intechsouthwest.ordermanagementsystem.workflowServices;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import com.intechsouthwest.ordermanagementsystem.dao.CustomerDao;
import com.intechsouthwest.ordermanagementsystem.domain.Customer;

/**
 * Created by gregorylaflash on 8/20/16.
 */
public class GetCustomerSalesRep implements JavaDelegate {

    @Autowired
    private CustomerDao customerDao;

    public GetCustomerSalesRep() {
        SpringApplicationContext.getContext().getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        String customerName = (String) execution.getVariable("customer");

        if(customerName == null || customerName.isEmpty())
            throw new RuntimeException("Customer not set on workflow");

        Customer customer = customerDao.findByName(customerName);

        String salesRep = customer.getSalesRepresentative();

        execution.setVariable("salesRep", salesRep);
    }
}
