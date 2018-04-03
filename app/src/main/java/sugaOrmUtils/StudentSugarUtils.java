package sugaOrmUtils;

import android.util.Log;

import com.duali.itouchpop2_test.utils.Globals;

import java.util.List;

import sUtils.StudentUtils;
import sugarorms.studentorm;

/**
 * Created by Mac on 2015/10/18.
 */
public class StudentSugarUtils {

    static boolean log = true;

    //GET STUDENT BY ACC NUMBER
    public static studentorm getStudentByAccNumber(String accnumber){
        log("++++++++++:getStudentByAccNumber");
        log("accnumber:"+accnumber);

        studentorm student = null;

        //Note.find(Note.class, "name = ? and title = ?", "satya", "title1");
        List<studentorm> studentlist = studentorm.find(studentorm.class, "accnumber = ?", accnumber);
        log("found students with that id:"+studentlist.size());

        //CHECK AND LOAD STUDENT
        if(studentlist.size() > 0){
            student = studentlist.get(0);
            log("loaded student:"+student.firstnmae);
        }

        return student;
    }

    //GET STUDENT BY ID
    public static studentorm getStudentById(int studentId){
        log("++++++++++:getStudentById");
        log("studentid:"+studentId);

        studentorm student = null;

        //Note.find(Note.class, "name = ? and title = ?", "satya", "title1");
        List<studentorm> studentlist = studentorm.find(studentorm.class, "studentid = ?", String.valueOf(studentId));
        log("found students with that id:"+studentlist.size());

        //CHECK AND LOAD STUDENT
        if(studentlist.size() > 0){
            student = studentlist.get(0);
            log("loaded student:"+student.firstnmae);
        }

        return student;
    }


    //GET STUDENT BY UDID
    public static studentorm getStudentByUdid(String udid){
        log("++++++++++:getStudentByUdid");
        log("udid:"+udid);

        studentorm student = null;

        //GET SHORTENED UDID
        String shortUdid = StudentUtils.shortUdid(udid);
        log("returned shortUdid:"+shortUdid.toLowerCase());

        //Note.find(Note.class, "name = ? and title = ?", "satya", "title1");
       List<studentorm> studentlist = studentorm.find(studentorm.class, "udid = ? OR udid = ? OR udid = ?", udid, shortUdid.toLowerCase(),shortUdid);

     //   List<studentorm> studentlist = studentorm.find(studentorm.class, "udid = ? OR udid = ?", udid, "fe 08 90 76");
        //TEST SHORT ONE
       // List<studentorm> studentlist = studentorm.find(studentorm.class, "udid = ?","fe 08 90 76");
       // List<studentorm> studentlist = studentorm.find(studentorm.class, "udid = ?", udid);
        log("found students with that udid:"+studentlist.size());


        //CHECK AND LOAD STUDENT
        if(studentlist.size() > 0){
            student = studentlist.get(0);
            log("loaded student:"+student.udid);
        }

        return student;
    }


    static void log(String msg) {
        if (log && Globals.log) {
            Log.v("StudentUtils", msg);
        }
    }
}
