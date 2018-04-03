package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.duali.itouchpop2_test.R;
import com.duali.itouchpop2_test.utils.Globals;

import java.util.ArrayList;
import java.util.List;

import mPock.Vars;
import sugaOrmUtils.StudentSugarUtils;
import sugarorms.studentorm;
import sugarorms.transactionsorm;

/**
 * Created by Mac on 2015/10/31.
 */
public class StudentAdapter extends BaseAdapter {

    public List<studentorm> studentList = new ArrayList<studentorm>();
    Vars vars;
    LayoutInflater inflater;
    Boolean log = true;

    public StudentAdapter(Vars vars, List<studentorm> studentList) {
        this.studentList = studentList;
        this.vars = vars;
        inflater = (LayoutInflater) vars.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return studentList.size();
    }

    @Override
    public Object getItem(int position) {
        return studentList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.canteen_row, null);
        TextView student_row_id = (TextView) vi.findViewById(R.id.student_row_id);
        TextView student_row_name = (TextView) vi.findViewById(R.id.student_row_name);
        TextView student_row_misc = (TextView) vi.findViewById(R.id.student_row_misc);


        student_row_id.setText(String.valueOf(studentList.get(position).studentid));
        student_row_name.setText(studentList.get(position).firstnmae+" "+studentList.get(position).lastname);
        student_row_misc.setText("BAL:" + String.valueOf(studentList.get(position).accbalance));


        return vi;
    }

    void log(String msg) {
        if (log && Globals.log) {
            Log.v(this.getClass().getSimpleName(), msg);
        }
    }

}
