package entities;

/**
 * Created by i on 6/15/16.
 */

import java.io.Serializable;
//import java.util.Collection;
import java.util.Date;

public class MealTransactions implements Serializable {


    private Student studentstudentId;

    private Devices devicesIddevices;
    private static final long serialVersionUID = 1L;


    private Integer idmealTransactions;

    private Date tapTimeBf;

    private Date tapTimeLunch;

    private Date tapTimeDinner;

    private Boolean lunch;

    private Boolean breakFast;

    private Boolean dinner;

    private Date dateTap;

    private Meals mealsIdmeals;

    public MealTransactions() {
    }

    public MealTransactions(Integer idmealTransactions) {
        this.idmealTransactions = idmealTransactions;
    }

    public Integer getIdmealTransactions() {
        return idmealTransactions;
    }

    public void setIdmealTransactions(Integer idmealTransactions) {
        this.idmealTransactions = idmealTransactions;
    }

    public Date getTapTimeBf() {
        return tapTimeBf;
    }

    public void setTapTimeBf(Date tapTimeBf) {
        this.tapTimeBf = tapTimeBf;
    }

    public Date getTapTimeLunch() {
        return tapTimeLunch;
    }

    public void setTapTimeLunch(Date tapTimeLunch) {
        this.tapTimeLunch = tapTimeLunch;
    }

    public Date getTapTimeDinner() {
        return tapTimeDinner;
    }

    public void setTapTimeDinner(Date tapTimeDinner) {
        this.tapTimeDinner = tapTimeDinner;
    }

    public Boolean getLunch() {
        return lunch;
    }

    public void setLunch(Boolean lunch) {
        this.lunch = lunch;
    }

    public Boolean getBreakFast() {
        return breakFast;
    }

    public void setBreakFast(Boolean breakFast) {
        this.breakFast = breakFast;
    }

    public Boolean getDinner() {
        return dinner;
    }

    public void setDinner(Boolean dinner) {
        this.dinner = dinner;
    }

    public Date getDateTap() {
        return dateTap;
    }

    public void setDateTap(Date dateTap) {
        this.dateTap = dateTap;
    }

    public Meals getMealsIdmeals() {
        return mealsIdmeals;
    }

    public void setMealsIdmeals(Meals mealsIdmeals) {
        this.mealsIdmeals = mealsIdmeals;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idmealTransactions != null ? idmealTransactions.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MealTransactions)) {
            return false;
        }
        MealTransactions other = (MealTransactions) object;
        if ((this.idmealTransactions == null && other.idmealTransactions != null) || (this.idmealTransactions != null && !this.idmealTransactions.equals(other.idmealTransactions))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.MealTransactions[ idmealTransactions=" + idmealTransactions + " ]";
    }

    public Student getStudentstudentId() {
        return studentstudentId;
    }

    public void setStudentstudentId(Student studentstudentId) {
        this.studentstudentId = studentstudentId;
    }

    public Devices getDevicesIddevices() {
        return devicesIddevices;
    }

    public void setDevicesIddevices(Devices devicesIddevices) {
        this.devicesIddevices = devicesIddevices;
    }
}
