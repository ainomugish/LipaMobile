package sugarorms;

/**
 * Created by i on 6/15/16.
 */

import android.util.Log;

import com.orm.SugarRecord;

import java.util.Date;

import entities.Meals;
import entities.Schools;

public class mealsorm extends SugarRecord {


    public String class1;

    //public Schools schoolsIdschools;//dont know about relationships


    public Integer idmeals;

    public Date mealsStartTime;

    public Date mealsEndTime;

    public String mealsStatus;

    public Date dateCreated;

    public String type;

    //public Integer schoolId;//if Schools does not work

    public mealsorm() {
    }


}
