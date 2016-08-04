package services.domain;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

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

    // private List<Item> items

    @Basic
    @Column(name = "total_price")
    private double totalPrice;
    @Basic
    @Column(name = "total_tax")
    private double totalTax;
    @Basic
    @Column(name = "total_shipping_charge")
    private String totalShippingCharge;

//    @Basic
//    @Column(name = "order_number")
//    private Customer customer;


    @Basic
    @Column(name = "shipping_address_street")
    private String shippingAddressStreet;
    @Basic
    @Column(name = "shipping_address_street2")
    private String shippingAddressStreet2;
    @Basic
    @Column(name = "shipping_address_city")
    private String shippingAddressCity;
    @Basic
    @Column(name = "shipping_address_state")
    private String shippingAddressState;
    @Basic
    @Column(name = "shipping_address_zip")
    private String shippingAddressZip;
    @Basic
    @Column(name = "shipping_address_is_po_box")
    private String shipping_address_is_po_box;

    @Basic
    @Column(name = "shipper")
    private String shipper;

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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(double totalTax) {
        this.totalTax = totalTax;
    }

    public String getTotalShippingCharge() {
        return totalShippingCharge;
    }

    public void setTotalShippingCharge(String totalShippingCharge) {
        this.totalShippingCharge = totalShippingCharge;
    }

    public Date getDatePlaced() {
        return datePlaced;
    }

    public void setDatePlaced(Date datePlaced) {
        this.datePlaced = datePlaced;
    }

    public String getShippingAddressStreet() {
        return shippingAddressStreet;
    }

    public void setShippingAddressStreet(String shippingAddressStreet) {
        this.shippingAddressStreet = shippingAddressStreet;
    }

    public String getShippingAddressStreet2() {
        return shippingAddressStreet2;
    }

    public void setShippingAddressStreet2(String shippingAddressStreet2) {
        this.shippingAddressStreet2 = shippingAddressStreet2;
    }

    public String getShippingAddressCity() {
        return shippingAddressCity;
    }

    public void setShippingAddressCity(String shippingAddressCity) {
        this.shippingAddressCity = shippingAddressCity;
    }

    public String getShippingAddressState() {
        return shippingAddressState;
    }

    public void setShippingAddressState(String shippingAddressState) {
        this.shippingAddressState = shippingAddressState;
    }

    public String getShippingAddressZip() {
        return shippingAddressZip;
    }

    public void setShippingAddressZip(String shippingAddressZip) {
        this.shippingAddressZip = shippingAddressZip;
    }

    public String getShipping_address_is_po_box() {
        return shipping_address_is_po_box;
    }

    public void setShipping_address_is_po_box(String shipping_address_is_po_box) {
        this.shipping_address_is_po_box = shipping_address_is_po_box;
    }

    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
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
}
