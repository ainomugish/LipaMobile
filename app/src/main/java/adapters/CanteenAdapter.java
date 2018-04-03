package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.duali.itouchpop2_test.R;

import java.util.ArrayList;
import java.util.List;

import mPock.Vars;
import sugarorms.transactionsorm;

/**
 * Created by Mac on 2015/10/31.
 */
public class CanteenAdapter  extends BaseAdapter {

    public List<transactionsorm> transactions = new ArrayList<transactionsorm>();
    Vars vars;
    LayoutInflater inflater;

    public CanteenAdapter(Vars vars, List<transactionsorm> theTrans) {
        this.transactions = theTrans;
        this.vars = vars;
        inflater = (LayoutInflater) vars.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
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
        TextView cant_transnum = (TextView) vi.findViewById(R.id.cant_transnum);
        TextView cant_transamount = (TextView) vi.findViewById(R.id.cant_transamount);
        TextView cant_balance = (TextView) vi.findViewById(R.id.cant_balance);
        TextView cant_studname  = (TextView) vi.findViewById(R.id.cant_studname);

        cant_transnum.setText(String.valueOf(transactions.get(position).getId()));
        cant_transamount.setText("Amnt:"+String.valueOf(transactions.get(position).amount));
        cant_balance.setText("BAL:"+String.valueOf(transactions.get(position).canteenbalanceafter));
        cant_studname.setText("STNDT:"+transactions.get(position).studentname);


        return vi;
    }

}