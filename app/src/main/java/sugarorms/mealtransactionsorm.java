package sugarorms;

/**
 * Created by i on 6/15/16.
 */


import android.util.Log;

import com.orm.SugarRecord;

import java.util.Date;

import entities.Devices;
import entities.MealTransactions;
import entities.Meals;
import entities.Student;


public class mealtransactionsorm extends SugarRecord {

    //public Student studentstudentId;

    //public Devices devicesIddevices;



    public Integer idmealTransactions;

    public Date tapTimeBf;

    public Date tapTimeLunch;

    public Date tapTimeDinner;

    public Boolean lunch;

    public Boolean breakFast;

    public Boolean dinner;

    public Date dateTap;

    public Integer studentId;
    public String studentudid;

    public String senttoserver;
    public String deviceTransId;
    public Integer mealsId;
    public Date sentdate;

    //public Meals mealsIdmeals;

    public mealtransactionsorm() {
    }
}
