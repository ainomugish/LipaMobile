/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
//import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Mac
 */

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer studentId;

    private String accNumber;

    private String firstName;

    private String lastName;

    private Integer accBalance;

    private Date studentDob;

    private Date dateCreated;

    private Date timeCreated;

    private String parentName;

    private String parentEmail;

    private String parentPhone;

    private String studentClass;

    private String udid;

    private ApiUsers apiUser;

    private Schools studentSchool;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "student")
//    private Collection<UpdateLog> updateLogCollection;

    public Student() {
    }

    public Student(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAccBalance() {
        return accBalance;
    }

    public void setAccBalance(Integer accBalance) {
        this.accBalance = accBalance;
    }

    public Date getStudentDob() {
        return studentDob;
    }

    public void setStudentDob(Date studentDob) {
        this.studentDob = studentDob;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public ApiUsers getApiUser() {
        return apiUser;
    }

    public void setApiUser(ApiUsers apiUser) {
        this.apiUser = apiUser;
    }

    public Schools getStudentSchool() {
        return studentSchool;
    }

    public void setStudentSchool(Schools studentSchool) {
        this.studentSchool = studentSchool;
    }

//    @XmlTransient
//    public Collection<UpdateLog> getUpdateLogCollection() {
//        return updateLogCollection;
//    }
//
//    public void setUpdateLogCollection(Collection<UpdateLog> updateLogCollection) {
//        this.updateLogCollection = updateLogCollection;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studentId != null ? studentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Student)) {
            return false;
        }
        Student other = (Student) object;
        if ((this.studentId == null && other.studentId != null) || (this.studentId != null && !this.studentId.equals(other.studentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Student[ studentId=" + studentId + " ]";
    }
    
}
