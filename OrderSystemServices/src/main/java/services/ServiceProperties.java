package services;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Created by gregorylaflash on 8/3/16.
 */
@ConfigurationProperties(prefix = "services")
public class ServiceProperties {

    @ConfigurationProperties("purchase-order")
    public class PurchaseOrder{
        public String fileSaveLocation;
    };
}
