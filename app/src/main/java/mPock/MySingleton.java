package mPock;

import android.content.Context;

import com.duali.itouchpop2_test.utils.Globals;
import com.orm.SugarContext;
import com.path.android.jobqueue.JobManager;

/**
 * Created by Mac on 2015/10/18.
 */
public class MySingleton {
    private static MySingleton instance = null;

    //FOR JOB QUER
    public JobManager jobManager;
    Context context;
    Vars vars;

    public String connectionStatus = Globals.DISCONNECTED;

    protected MySingleton(Context context) {
        // Exists only to defeat instantiation.
        //JOB QUEUE
        jobManager = new JobManager(context);
        this.vars = new Vars(context);
        this.context = context;

        //INIT SUGAR
        SugarContext.init(context);
    }

    public static MySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new MySingleton(context);
        }
        return instance;
    }
}