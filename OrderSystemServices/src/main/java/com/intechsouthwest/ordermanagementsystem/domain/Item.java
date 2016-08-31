package com.intechsouthwest.ordermanagementsystem.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by gregorylaflash on 8/14/16.
 */
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Basic
    @Column(name = "shipper")
    private String shipper;

    @Basic
    @NotNull
    @Column(name = "ship_to_client")
    private Boolean shipToClient;

    @Basic
    @Column(name = "is_from_stock")
    private Boolean isFromStock = false;
}
