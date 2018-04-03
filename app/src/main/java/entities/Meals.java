package entities;

/**
 * Created by i on 6/15/16.
 */



import java.io.Serializable;
//import java.util.Collection;
import java.util.Date;


public class Meals implements Serializable {


    private String class1;

    private Schools schoolsIdschools;
    private static final long serialVersionUID = 1L;

    private Integer idmeals;

    private Date mealsStartTime;

    private Date mealsEndTime;

    private String mealsStatus;

    private Date dateCreated;

    private String type;

    public Meals() {
    }

    public Meals(Integer idmeals) {
        this.idmeals = idmeals;
    }

    public Integer getIdmeals() {
        return idmeals;
    }

    public void setIdmeals(Integer idmeals) {
        this.idmeals = idmeals;
    }

    public Date getMealsStartTime() {
        return mealsStartTime;
    }

    public void setMealsStartTime(Date mealsStartTime) {
        this.mealsStartTime = mealsStartTime;
    }

    public Date getMealsEndTime() {
        return mealsEndTime;
    }

    public void setMealsEndTime(Date mealsEndTime) {
        this.mealsEndTime = mealsEndTime;
    }

    public String getMealsStatus() {
        return mealsStatus;
    }

    public void setMealsStatus(String mealsStatus) {
        this.mealsStatus = mealsStatus;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idmeals != null ? idmeals.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Meals)) {
            return false;
        }
        Meals other = (Meals) object;
        if ((this.idmeals == null && other.idmeals != null) || (this.idmeals != null && !this.idmeals.equals(other.idmeals))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Meals[ idmeals=" + idmeals + " ]";
    }

    public Schools getSchoolsIdschools() {
        return schoolsIdschools;
    }

    public void setSchoolsIdschools(Schools schoolsIdschools) {
        this.schoolsIdschools = schoolsIdschools;
    }

    public String getClass1() {
        return class1;
    }

    public void setClass1(String class1) {
        this.class1 = class1;
    }
}
