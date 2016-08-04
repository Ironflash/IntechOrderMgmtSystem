package services.dao;

import org.springframework.data.repository.CrudRepository;
import services.domain.Customer;
import services.domain.PurchaseOrder;

/**
 * Created by gregorylaflash on 7/31/16.
 */
public interface CustomerDao extends CrudRepository<Customer, String> {
    Customer findByName(String name);
}
