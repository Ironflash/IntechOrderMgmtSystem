package services.dao;

import org.springframework.data.repository.CrudRepository;
import services.domain.PurchaseOrder;

/**
 * Created by gregorylaflash on 7/31/16.
 */
public interface PurchaseOrderDao extends CrudRepository<PurchaseOrder, String> {
    PurchaseOrder findByOrderID(String orderID);
}
