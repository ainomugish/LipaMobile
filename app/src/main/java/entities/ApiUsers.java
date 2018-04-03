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

public class ApiUsers implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idapiUsers;

    private String name;

    private String apiKey;

    private Date dateCreated;

    private Date timeCreated;

    private String valid;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "apiUser")
//    private Collection<Student> studentCollection;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "apiUsers")
//    private Collection<UpdateLog> updateLogCollection;

    public ApiUsers() {
    }

    public ApiUsers(Integer idapiUsers) {
        this.idapiUsers = idapiUsers;
    }

    public Integer getIdapiUsers() {
        return idapiUsers;
    }

    public void setIdapiUsers(Integer idapiUsers) {
        this.idapiUsers = idapiUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
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

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

//    @XmlTransient
//    public Collection<Student> getStudentCollection() {
//        return studentCollection;
//    }
//
//    public void setStudentCollection(Collection<Student> studentCollection) {
//        this.studentCollection = studentCollection;
//    }
//
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
        hash += (idapiUsers != null ? idapiUsers.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ApiUsers)) {
            return false;
        }
        ApiUsers other = (ApiUsers) object;
        if ((this.idapiUsers == null && other.idapiUsers != null) || (this.idapiUsers != null && !this.idapiUsers.equals(other.idapiUsers))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ApiUsers[ idapiUsers=" + idapiUsers + " ]";
    }
    
}
