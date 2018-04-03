/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
//import java.util.Collection;
import java.util.Date;



public class Schools implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idschools;

    private String schoolName;

    private Date dateCreated;

    private Date timeCreated;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studentSchool")
//    private Collection<Student> studentCollection;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bursarSchool")
//    private Collection<Bursars> bursarsCollection;

    public Schools() {
    }

    public Schools(Integer idschools) {
        this.idschools = idschools;
    }

    public Integer getIdschools() {
        return idschools;
    }

    public void setIdschools(Integer idschools) {
        this.idschools = idschools;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
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
//
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
//    public Collection<Bursars> getBursarsCollection() {
//        return bursarsCollection;
//    }
//
//    public void setBursarsCollection(Collection<Bursars> bursarsCollection) {
//        this.bursarsCollection = bursarsCollection;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idschools != null ? idschools.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Schools)) {
            return false;
        }
        Schools other = (Schools) object;
        if ((this.idschools == null && other.idschools != null) || (this.idschools != null && !this.idschools.equals(other.idschools))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Schools[ idschools=" + idschools + " ]";
    }
    
}
