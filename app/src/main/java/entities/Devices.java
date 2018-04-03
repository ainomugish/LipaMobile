/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author ivna
 */
public class Devices implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer iddevices;

    private String macaddress;

    private Date dateAdded;
    private Integer devicePin;
    private Integer balance;
    private Collection<CanteenTrans> canteenTransCollection;
    private Schools schoolsIdschools;

    public Devices() {
    }

    public Devices(Integer iddevices) {
        this.iddevices = iddevices;
    }

    public Integer getIddevices() {
        return iddevices;
    }

    public void setIddevices(Integer iddevices) {
        this.iddevices = iddevices;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Integer getDevicePin() {
        return devicePin;
    }

    public void setDevicePin(Integer devicePin) {
        this.devicePin = devicePin;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }


    public Collection<CanteenTrans> getCanteenTransCollection() {
        return canteenTransCollection;
    }

    public void setCanteenTransCollection(Collection<CanteenTrans> canteenTransCollection) {
        this.canteenTransCollection = canteenTransCollection;
    }

    public Schools getSchoolsIdschools() {
        return schoolsIdschools;
    }

    public void setSchoolsIdschools(Schools schoolsIdschools) {
        this.schoolsIdschools = schoolsIdschools;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iddevices != null ? iddevices.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Devices)) {
            return false;
        }
        Devices other = (Devices) object;
        if ((this.iddevices == null && other.iddevices != null) || (this.iddevices != null && !this.iddevices.equals(other.iddevices))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Devices[ iddevices=" + iddevices + " ]";
    }

}
