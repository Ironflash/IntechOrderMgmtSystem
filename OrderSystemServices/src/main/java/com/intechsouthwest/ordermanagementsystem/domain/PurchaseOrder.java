package com.intechsouthwest.ordermanagementsystem.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created by gregorylaflash on 7/31/16.
 */
@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {


    @Basic
    @NotNull
    @Id
    @Column(name = "order_id",nullable = false)
    private String orderID;

    @Basic
    @NotNull
    @Column(name = "date_placed",nullable = false)
    private Date datePlaced = new Date();

    @Basic
    @NotNull
    @Column(name = "scanned_pdf",nullable = false)
    private String scannedPDFLocation;

    @Basic
    @NotNull
    @Column(name = "customer_purchase_order_id",nullable = false)
    private String customerPurchaseOrderID;

    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name = "purchase_order")
    private Set<Item> items = new HashSet<>();

    @Basic
    @Column(name = "wf_instance_id")
    private String wfInstanceID;

//    List<Note> notes



    public PurchaseOrder() {}

    public PurchaseOrder(String scannedPDFLocation, String customerPurchaseOrderNumber) {
        this.orderID = UUID.randomUUID().toString();
        this.datePlaced = new Date();
        this.scannedPDFLocation = scannedPDFLocation;
        this.customerPurchaseOrderID = customerPurchaseOrderNumber;
    }
    // getters/setters

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Date getDatePlaced() {
        return datePlaced;
    }

    public void setDatePlaced(Date datePlaced) {
        this.datePlaced = datePlaced;
    }

    public String getScannedPDFLocation() {
        return scannedPDFLocation;
    }

    public void setScannedPDFLocation(String scannedPDFLocation) {
        this.scannedPDFLocation = scannedPDFLocation;
    }

    public String getCustomerPurchaseOrderID() {
        return customerPurchaseOrderID;
    }

    public void setCustomerPurchaseOrderID(String customerPurchaseOrderID) {
        this.customerPurchaseOrderID = customerPurchaseOrderID;
    }

    public String getWfInstanceID() {
        return wfInstanceID;
    }

    public void setWfInstanceID(String wfInstanceID) {
        this.wfInstanceID = wfInstanceID;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }
}
