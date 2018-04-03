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
 * @author Mac
 */
public class StudentCredz implements Serializable {

    private Integer changeLogNum;

    private Date dateMod;

    private Date timeMod;
    private static final long serialVersionUID = 1L;

    private Integer idstudentCredz;

    private Integer studentId;

    private String studentUsername;

    private String studentPassword;

    private String studentPin;

    public StudentCredz() {
    }

    public StudentCredz(Integer idstudentCredz) {
        this.idstudentCredz = idstudentCredz;
    }

    public Integer getIdstudentCredz() {
        return idstudentCredz;
    }

    public void setIdstudentCredz(Integer idstudentCredz) {
        this.idstudentCredz = idstudentCredz;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public String getStudentPassword() {
        return studentPassword;
    }

    public void setStudentPassword(String studentPassword) {
        this.studentPassword = studentPassword;
    }

    public String getStudentPin() {
        return studentPin;
    }

    public void setStudentPin(String studentPin) {
        this.studentPin = studentPin;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idstudentCredz != null ? idstudentCredz.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudentCredz)) {
            return false;
        }
        StudentCredz other = (StudentCredz) object;
        if ((this.idstudentCredz == null && other.idstudentCredz != null) || (this.idstudentCredz != null && !this.idstudentCredz.equals(other.idstudentCredz))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.StudentCredz[ idstudentCredz=" + idstudentCredz + " ]";
    }

    public Integer getChangeLogNum() {
        return changeLogNum;
    }

    public void setChangeLogNum(Integer changeLogNum) {
        this.changeLogNum = changeLogNum;
    }

    public Date getDateMod() {
        return dateMod;
    }

    public void setDateMod(Date dateMod) {
        this.dateMod = dateMod;
    }

    public Date getTimeMod() {
        return timeMod;
    }

    public void setTimeMod(Date timeMod) {
        this.timeMod = timeMod;
    }

}
