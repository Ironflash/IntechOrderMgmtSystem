package services.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * Created by gregorylaflash on 8/2/16.
 */
@Embeddable
public class Address {

    @Basic
    @NotNull
    @Column(name = "street")
    private String street;
    @Basic
    @Column(name = "street2")
    private String street2;
    @Basic
    @NotNull
    @Column(name = "city")
    private String city;
    @Basic
    @NotNull
    @Column(name = "state")
    private String state;
    @Basic
    @NotNull
    @Column(name = "zip")
    private String zip;
    @Basic
    @NotNull
    @Column(name = "is_po_box")
    private boolean isPOBox;


    public Address() {}

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public boolean isPOBox() {
        return isPOBox;
    }

    public void setPOBox(boolean POBox) {
        isPOBox = POBox;
    }
}
