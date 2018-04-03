package eventBusClasses;

import android.util.Log;

import com.duali.itouchpop2_test.utils.Globals;

import entities.Student;
import sugarorms.studentorm;


/**
 * Created by Mac on 2015/12/06.
 */
public class SendStudent {

    public boolean log = true;
    public studentorm sentStudent;
    public String transId;

    public SendStudent(studentorm sentStudent, String transId){
        log("++++++++:SendStudent created ");
        log("student:"+sentStudent.lastname);
        log("transId:"+transId);

        this.sentStudent = sentStudent;
        this.transId = transId;
      //  log("PubMessage sentMessage recep:"+sm.recep);
       // this.sm = sm;

    }

    void log(String msg) {
        if (log && Globals.log) {
            Log.v(this.getClass().getSimpleName(), msg);
        }
    }
}
