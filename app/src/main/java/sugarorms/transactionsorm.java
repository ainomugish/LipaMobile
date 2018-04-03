package sugarorms;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by ivan on 2015/10/31.
 */
public class transactionsorm extends SugarRecord {

    public Date transdate;
    public Date sentdate;
    public int amount;

    public int canteenbalancebefore;
    public int canteenbalanceafter;

    //STUDENT INFO
    public int studentbalancebefore;
    public int studentbalanceafter;
    public int studentid;
    public String studentname;
    public String studentudid;
    public String studentjson;

    public String senttoserver;
    public String deviceTransId;

    //EXTRAS
    public String extrastringone;
    public String extrastringtwo;
    public String extrastringthree;

    public int extraintone;
    public int extrainttwo;
    public int extrainnthree;


    public transactionsorm() {

    }
}
