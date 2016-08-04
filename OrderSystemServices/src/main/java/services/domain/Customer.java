package services.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gregorylaflash on 8/2/16.
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Basic
    @NotNull
    private String name;

    @Embedded
    private Address billingAddress;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="firstName", column = @Column(name="rep_first_name") ),
            @AttributeOverride(name="lastName", column = @Column(name="rep_last_name") ),
            @AttributeOverride(name="cellPhoneNumber", column = @Column(name="rep_cell_phone") ),
            @AttributeOverride(name="workPhoneNumber", column = @Column(name="rep_work_phone") ),
            @AttributeOverride(name="faxNumber", column = @Column(name="rep_fax") ),
            @AttributeOverride(name="personalEmail", column = @Column(name="rep_personal_email") ),
            @AttributeOverride(name="workEmail", column = @Column(name="rep_work_email") )
    } )
    private CustomerContact representativeContact;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="firstName", column = @Column(name="tech_support_first_name") ),
            @AttributeOverride(name="lastName", column = @Column(name="tech_support_last_name") ),
            @AttributeOverride(name="cellPhoneNumber", column = @Column(name="tech_support_cell_phone") ),
            @AttributeOverride(name="workPhoneNumber", column = @Column(name="tech_support_work_phone") ),
            @AttributeOverride(name="faxNumber", column = @Column(name="tech_support_fax") ),
            @AttributeOverride(name="personalEmail", column = @Column(name="tech_support_personal_email") ),
            @AttributeOverride(name="workEmail", column = @Column(name="tech_support_work_email") )
    } )
    private CustomerContact technicalSupportContact;

    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name = "customer_name")
    private List<PurchaseOrder> purchaseOrders;

//    quotes


    public Customer() {
        purchaseOrders = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public CustomerContact getRepresentativeContact() {
        return representativeContact;
    }

    public void setRepresentativeContact(CustomerContact representativeContact) {
        this.representativeContact = representativeContact;
    }

    public CustomerContact getTechnicalSupportContact() {
        return technicalSupportContact;
    }

    public void setTechnicalSupportContact(CustomerContact technicalSupportContact) {
        this.technicalSupportContact = technicalSupportContact;
    }

    // -------------

    public void addPurchaseOrder(PurchaseOrder po) {
        purchaseOrders.add(po);
    }
}
