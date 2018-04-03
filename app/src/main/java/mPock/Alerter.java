package mPock;

/**
 * Created by ivan on 10/21/15.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.duali.itouchpop2_test.R;

import me.drakeet.materialdialog.MaterialDialog;


public class Alerter {
    Context context;

    Boolean log = false;

    MaterialDialog mMaterialDialog;

    public Alerter(Context context) {
        this.context = context;
    }


    //RESET STUDENTS.
    public void alerterResetStudents(String header, String message) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.dialog_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);

        headerTxt.setText(header);
        messageTxt.setText(message);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();

                        //RESET STUDENTS AND START NEW GET STUDENTS JOB

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    // DIALOG THAT SHOWS ERROR
    public void alerterAny(String header, String message) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.dialog_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);

        headerTxt.setText(header);
        messageTxt.setText(message);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();
                        dialog.cancel();
                        // Intent i = new Intent(context, daClassL);
                        // // i.setClass(context, daClass.class);
                        // context.startActivity(i);
                    }
                });
        // .setNegativeButton("Cancel",
        // new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog,int id) {
        // dialog.cancel();
        // }
        // });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    // DIALOG THAT SHOWS ERROR
    public void alerterAny2(String header, String message) {


        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.dialog_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);

        headerTxt.setText(header);
        messageTxt.setText(message);


        // final MaterialDialog finalMMaterialDialog = mMaterialDialog;
        mMaterialDialog = new MaterialDialog(context)
                .setTitle(header)
                .setMessage(message)
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();

                    }
                })
                .setNegativeButton("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();

                    }
                });

        mMaterialDialog.show();
        //  }
    }

    public void alerterSuccessActivityCancel(String header, String message,
                                             final Class<?> daClassL, final String intentName,
                                             final String intentString) {
        // public void alerterSuccessActivity(String head, er, String message,
        // final Activity act){

        // Class<?> daClass = daClassL;

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.dialog_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);

        headerTxt.setText(header);
        messageTxt.setText(message);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();
                        dialog.cancel();
                        Intent i = new Intent(context, daClassL);
                        i.putExtra(intentName, intentString);
                        // i.setClass(context, daClass.class);
                        context.startActivity(i);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    // DIALOG THAT ALLOWS EXIT TO ANY SENT ACTIVITY WHEN OK IS PRESSED
    public void alerterAnySuccessActivity(String header, String message,
                                          final Class<?> daClassL) {
        // public void alerterSuccessActivity(String head, er, String message,
        // final Activity act){

        // Class<?> daClass = daClassL;

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.dialog_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);

        headerTxt.setText(header);
        messageTxt.setText(message);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();
                        dialog.cancel();
                        Intent i = new Intent(context, daClassL);
                        // i.setClass(context, daClass.class);
                        context.startActivity(i);
                    }
                });
        // .setNegativeButton("Cancel",
        // new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog,int id) {
        // dialog.cancel();
        // }
        // });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    public void alerterAnySuccessActivity2(String header, String message, final Class<?> daClassL) {


        LayoutInflater li = LayoutInflater.from(context);
        View promptsView;
        promptsView = li.inflate(R.layout.dialog_success_simple, null);

        TextView headerTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_header);
        TextView messageTxt = (TextView) promptsView
                .findViewById(R.id.success_simple_message);

        headerTxt.setText(header);
        messageTxt.setText(message);


        // final MaterialDialog finalMMaterialDialog = mMaterialDialog;
        mMaterialDialog = new MaterialDialog(context)
                .setTitle(header)
                .setMessage(message)
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        Intent i = new Intent(context, daClassL);
                        context.startActivity(i);

                    }
                })
                .setNegativeButton("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();

                    }
                });

        mMaterialDialog.show();
        //  }
    }
    // DIALOG THAT ALLOWS EXIT TO ANY SENT ACTIVITY WHEN OK IS PRESSED

    // DIALOG THAT ALLOWS EXIT TO ANY SENT ACTIVITY WHEN OK IS PRESSED
    /*public void alerterSuccessActivityCancel(String header, String message,
                                             final Class<?> daClassL, final String intentName,
                                             final String intentString) {

        final MaterialDialog.Builder al_dialog = new MaterialDialog.Builder(context);
        al_dialog.title(header);

        al_dialog.content(message);

        al_dialog.positiveText(context.getString(R.string.continue_text));
        al_dialog.negativeText(context.getString(R.string.cancel));
        al_dialog.cancelable(false);
        al_dialog.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                dialog.cancel();
                Intent i = new Intent(context, daClassL);
                i.putExtra(intentName, intentString);

                context.startActivity(i);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);//
                dialog.cancel();

            }
        }).build();
        al_dialog.show();
    }*/

    // DIALOG THAT ALLOWS EXIT TO ANY SENT ACTIVITY WHEN OK IS PRESSED
  /*  public void alerterSuccessActivity(String header, String message,
                                       final Class<?> daClassL, final String intentName,
                                       final String intentString) {

        final MaterialDialog.Builder al_dialog = new MaterialDialog.Builder(context);
        al_dialog.title(header);

        al_dialog.content(message);
        al_dialog.positiveText(context.getString(R.string.okay));
        al_dialog.cancelable(false);
        al_dialog.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                dialog.cancel();
                Intent i = new Intent(context, daClassL);
                i.putExtra(intentName, intentString);

                context.startActivity(i);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);//
                dialog.cancel();

            }
        }).build();
        al_dialog.show();
    }
    //   }


    // DIALOG THAT ALLOWS EXIT TO ANY SENT ACTIVITY WHEN OK IS PRESSED
*//*    public void alerterSuccessFragment(String header, String message,
                                       final Fragment daFrag, final String intentName,
                                       final String intentString) {

        final MaterialDialog.Builder al_dialog = new MaterialDialog.Builder(context);
        al_dialog.title(header);

        al_dialog.content(message);
        al_dialog.positiveText(context.getString(R.string.okay));
        al_dialog.cancelable(false);
        al_dialog.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog){
                dialog.cancel();

                Fragment newFragment = daFrag;
                        FragmentManager fragmentManager = context.;
                        fragmentManager.beginTransaction().replace(R.id.content_frame, newFragment).commit();

                context.startActivity(i);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);//
                dialog.cancel();

            }
        }).build();
        al_dialog.show();
    }*/
}



