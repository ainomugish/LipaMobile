package sUtils;

import android.util.Log;

import com.duali.itouchpop2_test.utils.Globals;

import java.util.Date;

import entities.CanteenTrans;
import entities.Devices;
import entities.Student;
import mPock.Vars;
import sugarorms.studentorm;
import sugarorms.transactionsorm;

/**
 * Created by Mac on 2015/10/18.
 */
public class StudentUtils {

    static boolean log = true;

    public static CanteenTrans  createCanteenTransFromTorm(Vars vars,transactionsorm torm){
        log("+++++++++++++:createCanteenTransFromTorm");

        CanteenTrans ct = new CanteenTrans();
        ct.setAmount(torm.amount);
        ct.setCanteenbalanceafter(torm.canteenbalanceafter);
        ct.setCanteenbalancebefore(torm.canteenbalancebefore);
        ct.setDateSent(new Date());
        //SET DEVICE
        Devices device = new Devices();
        device.setMacaddress(vars.macAddress);
        ct.setDevice(device);
        //SET STUDENT
        Student student2 = new Student();
        student2.setStudentId(torm.studentid);
        ct.setStudent(student2);
        ct.setStudentbalanceafter(torm.studentbalanceafter);
        ct.setStudentbalancebefore(String.valueOf(torm.studentbalancebefore));
        ct.setTransDate(new Date());
        ct.setDeviceTransId(torm.deviceTransId);

        return ct;

    }

    //GET SHORTENED UDID
    public static String shortUdid(String udid){
        log("+++++++++++: shortUdid");
        log("sentUdid:"+udid);

        //REMOVE HEADER
        String sUdid = udid.replace("4D00", "");
        System.out.println("header removed:" + sUdid);

        StringBuilder sb = new StringBuilder();
//
        char[] c = sUdid.toCharArray();
        for (int i = 0; i < 12; i++) {
            System.out.println("loop:" + i);

            //9E958F76

            if(i==0){
                System.out.println("i="+i);
                sb.append(c[i]);
            }else if(i==1){
                System.out.println("i="+i);
                sb.append(c[i]);
            }else if(i==2){
                System.out.println("i="+i);
                sb.append(" ");
            } else if(i==3){
                System.out.println("i="+i);
                sb.append(c[i-1]);
            }else if(i==4){
                System.out.println("i="+i);
                sb.append(c[i-1]);
            }else if(i==5){
                System.out.println("i="+i);
                sb.append(" ");
            }else if(i==6){
                System.out.println("i="+i);
                sb.append(c[i-2]);
            }else if(i==7){
                System.out.println("i="+i);
                sb.append(c[i-2]);
            }else if(i==8){
                System.out.println("i="+i);
                sb.append(" ");
            }else if(i==9){
                System.out.println("i="+i);
                sb.append(c[6]);
            }else if(i==10){
                System.out.println("i="+i);
                sb.append(c[7]);
            }


            System.out.println(sb.toString().trim());
        }

        System.out.println(sb.toString().trim());

        log("final String:"+sb.toString().trim());
        return sb.toString().trim();

    }

    //RESETS BALANCE TO ZERO IF ITS NULL
    public static int getBalance(studentorm student){
       log("+++++++ getstudentbalance");

        if(student.accbalance == 0){
            log("acc balance is null or zero");
            return 0;
        }else{
            return student.accbalance;
        }
    }

    public static String findStudent(String udid){
        log("sent student id:"+udid);

        if(udid.equalsIgnoreCase("4D008E4A9876")){
            return "muwhezi";
        }else if(udid.equalsIgnoreCase("4D009E958F76")){
            return "mukuye";
        }else{
            return "unknown Student";
        }
    }

    static void log(String msg) {
        if (log && Globals.log) {
            Log.v("StudentUtils", msg);
        }
    }
}
