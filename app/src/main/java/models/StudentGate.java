package models;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import sugarorms.studentorm;

/**
 * Created by Mac on 2016/03/17.
 */
public class StudentGate {

    //public Gson gson;
    static Gson toStringGson = new GsonBuilder().setPrettyPrinting().create();

    public  int studentid;

    public  String accnumber;

    public String firstnmae;

    public String lastname;



    public Date studentdob;

    public Date datecreated;

    public Date timecreated;

    public String parentName;

    public String parentemail;

    public String parentphone;

    public String studentclass;

    public String udid;

    public int pin;



    public StudentGate(studentorm sm){
        log("++++++++++++: StudentGate CONSTRUCTOR");

        this.studentid = sm.studentid;

        this.accnumber = sm.accnumber;

        this.firstnmae = sm.firstnmae;

        this.lastname = sm.lastname;

        // this.accbalance;

        //this.studentdob = sm.studentdob;

        // this.datecreated = sm.datecreated;

        // this.timecreated = sm.timecreated;

        //this.parentName = sm.parentName;

        //  this.parentemail = sm.parentemail;

        //  this.parentphone = sm.parentphone;

        this.studentclass = sm.studentclass;

        this.udid = sm.udid;


    }

    void log(String msg) {
        //if (log && Globals.log) {
        Log.v(this.getClass().getSimpleName(), msg);
        //}
    }


    @Override
    public String toString() {
        return toStringGson.toJson(this);
    }

}
