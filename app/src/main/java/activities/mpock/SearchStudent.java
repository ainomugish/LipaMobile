package activities.mpock;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.duali.itouchpop2_test.R;
import com.duali.itouchpop2_test.RFActivity;
import com.duali.itouchpop2_test.utils.Globals;

import mPock.Vars;
import sugaOrmUtils.StudentSugarUtils;
import sugarorms.studentorm;

public class SearchStudent extends ActionBarActivity {

    Vars vars;

    EditText search_studentId;

    boolean log = true;

    studentorm student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_student);

        vars = new Vars(this);
        search_studentId = (EditText) findViewById(R.id.search_studentId);
    }

    public void goBack(View v){
        log("+++++++: goback");
        startActivity(new Intent(this, RFActivity.class));
    }

    public void search(View v){
        log("++++++++++: search");
        if(search_studentId.getText().toString().length() < 1){
            vars.alerter.alerterAny("Error", "Please enter student id");
        }else{
            String studentId = search_studentId.getText().toString();
            log("got studentid:"+studentId);

            //QUERY FOR ID
            student = StudentSugarUtils.getStudentById(Integer.valueOf(studentId));
            if(student != null){

                vars.alerter.alerterAny("Success:",student.lastname+":\n"+student.udid+":\n"+student.accnumber+":\n"+student.accbalance);
              //  vars.alerter.alerterAny("Success:",student.udid);

            }else{
                vars.alerter.alerterAny("Error","NO STUDENT FOUND WITH ID:"+studentId);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void log(String msg) {
        if (log && Globals.log) {
            Log.v(this.getClass().getSimpleName(), msg);
        }
    }

}
