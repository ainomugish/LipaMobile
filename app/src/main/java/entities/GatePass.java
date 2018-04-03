package entities;

/**
 * Created by i on 5/3/16.
 */

import java.io.Serializable;
//import java.util.Collection;
import java.util.Date;



public class GatePass implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idgatePass;

    private String reason;

    private Date startDate;

    private Date endDate;

    private Date startTime;

    private Date endTime;

    private Boolean permission;

    private String accNumber;

    private Date dateCreated;


    private Devices device;

    private Student student;

    public GatePass() {
    }
    public GatePass(Integer idgatePass) {
        this.idgatePass = idgatePass;
    }

    public Integer getIdgatePass() {
        return idgatePass;
    }

    public void setIdgatePass(Integer idgatePass) {
        this.idgatePass = idgatePass;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Boolean getPermission() {
        return permission;
    }

    public void setPermission(Boolean permission) {
        this.permission = permission;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Devices getDevice() {
        return device;
    }

    public void setDevice(Devices device) {
        this.device = device;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idgatePass != null ? idgatePass.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GatePass)) {
            return false;
        }
        GatePass other = (GatePass) object;
        if ((this.idgatePass == null && other.idgatePass != null) || (this.idgatePass != null && !this.idgatePass.equals(other.idgatePass))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.GatePass[ idgatePass=" + idgatePass + " ]";
    }

}
