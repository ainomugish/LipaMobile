/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
/**
 *
 * @author ivan
 */
public class CanteenTrans implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idCanteenTrans;

    private Date dateSent;

    private Date timeSent;
    private Integer amount;

    private String canteenTranscol;

    private Date transDate;
    private Integer canteenbalancebefore;

    private Integer canteenbalanceafter;

    private String studentbalancebefore;
    private Integer studentbalanceafter;
    private Devices device;
    private Student student;
    private String deviceTransId;

    public CanteenTrans() {
    }

    public CanteenTrans(Integer idCanteenTrans) {
        this.idCanteenTrans = idCanteenTrans;
    }

    public Integer getIdCanteenTrans() {
        return idCanteenTrans;
    }

    public void setIdCanteenTrans(Integer idCanteenTrans) {
        this.idCanteenTrans = idCanteenTrans;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCanteenTranscol() {
        return canteenTranscol;
    }

    public void setCanteenTranscol(String canteenTranscol) {
        this.canteenTranscol = canteenTranscol;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public Integer getCanteenbalancebefore() {
        return canteenbalancebefore;
    }

    public void setCanteenbalancebefore(Integer canteenbalancebefore) {
        this.canteenbalancebefore = canteenbalancebefore;
    }

    public Integer getCanteenbalanceafter() {
        return canteenbalanceafter;
    }

    public void setCanteenbalanceafter(Integer canteenbalanceafter) {
        this.canteenbalanceafter = canteenbalanceafter;
    }

    public String getStudentbalancebefore() {
        return studentbalancebefore;
    }

    public void setStudentbalancebefore(String studentbalancebefore) {
        this.studentbalancebefore = studentbalancebefore;
    }

    public Integer getStudentbalanceafter() {
        return studentbalanceafter;
    }

    public void setStudentbalanceafter(Integer studentbalanceafter) {
        this.studentbalanceafter = studentbalanceafter;
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

    public String getDeviceTransId() {
        return deviceTransId;
    }

    public void setDeviceTransId(String deviceTransId) {
        this.deviceTransId = deviceTransId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCanteenTrans != null ? idCanteenTrans.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CanteenTrans)) {
            return false;
        }
        CanteenTrans other = (CanteenTrans) object;
        if ((this.idCanteenTrans == null && other.idCanteenTrans != null) || (this.idCanteenTrans != null && !this.idCanteenTrans.equals(other.idCanteenTrans))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.CanteenTrans[ idCanteenTrans=" + idCanteenTrans + " ]";
    }

}
