package com.intechsouthwest.ordermanagementsystem;

import org.springframework.boot.context.properties.ConfigurationProperties;

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
