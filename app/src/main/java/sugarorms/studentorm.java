package sugarorms;

import android.util.Log;

import com.orm.SugarRecord;

import java.util.Date;

import entities.Student;

/**
 * Created by Mac on 2015/10/18.
 */
public class studentorm extends SugarRecord{
  
    public  int studentid;

    public  String accnumber;

    public String firstnmae;

    public String lastname;

    public int accbalance;

    public Date studentdob;

    public Date datecreated;

    public Date timecreated;

    public String parentName;

    public String parentemail;

    public String parentphone;

    public String studentclass;

    public String udid;

    public int pin;

    //SCHOOL NAME

    public int schoolId;

//    public ApiUsers apiUser;
//    @JoinColumn(name = "student_school", referencedColumnName = "idschools")
//    @ManyToOne(optional = false)
    public String studentschoolname;
    public String schooljson;

    //EXTRAS
    public String extrastringone;
    public String extrastringtwo;
    public String extrastringthree;

    public int extraintone;
    public int extrainttwo;
    public int extrainnthree;


        public studentorm(){

        }

        public studentorm(Student student){
            log("++++++++++ student orm constructor");

            //student id
            if(student.getStudentId() != null){
                log("cid received studentid:"+student.getStudentId());
                this.studentid = student.getStudentId();
                log("cid student id on created object:"+this.studentid);
            }

            //ACC NUMBER
            if(student.getAccNumber() != null){
                this.accnumber = student.getAccNumber();
            }

            //first name
            if(student.getFirstName() != null){
                this.firstnmae = student.getFirstName();
            }

            //last name
            if(student.getLastName() != null){
                this.lastname = student.getLastName();
            }

            //ACC BALANCE
            if(student.getAccBalance() != null){
                this.accbalance = student.getAccBalance();
            }

            this.datecreated = new Date();
            this.timecreated = new Date();

            //PARENT NAME
            if(student.getParentName() != null){
                this.parentName = student.getParentName();
            }

            //PARENT EMAIL
            if(student.getParentEmail() != null){
                this.parentemail = student.getParentName();
            }

            //PARENT PHONE
            if(student.getParentPhone() != null){
                this.parentphone = student.getParentPhone();
            }

            //STUDENT CLASS
            if(student.getStudentClass() != null){
                this.studentclass = student.getStudentClass();
            }

            //STUDENT UDID
            if(student.getUdid()!= null){
                this.udid = student.getUdid();
            }

            //SCHOOL INFO
            if(student.getStudentSchool().getIdschools() != null){
                this.schoolId = student.getStudentSchool().getIdschools();
            }

            if(student.getStudentSchool().getSchoolName() != null){
                this.studentschoolname = student.getStudentSchool().getSchoolName();
            }else{
                this.studentschoolname = "N/A";
            }

            //PIN NUMBER


            log("student created");
        }

    void log(String msg) {
        //if (log && Globals.log) {
        Log.v(this.getClass().getSimpleName(), msg);
        //}
    }

}
