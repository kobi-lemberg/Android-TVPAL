package com.tvpal.kobi.tvpal.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.tvpal.kobi.tvpal.MyApplication;
import com.tvpal.kobi.tvpal.NewsFeedActivity;


public class StringDialogFragment extends DialogFragment {
    String strToShow;
    MyDialogInterface i;
    public interface MyDialogInterface{
        public void onConfirmed();
    }
    public void setStrToShowAndListener(String strToShow,MyDialogInterface i) {
        this.strToShow = strToShow;
        this.i=i;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(strToShow)
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Log.d("SuccessDialogFragment", "SuccessDialogFragment");
                        i.onConfirmed();
                        dialog.cancel();
                    }
                });
        builder.setCancelable(true);
        return builder.create();
    }
}
