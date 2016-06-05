package com.tvpal.kobi.tvpal.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Kobi on 12/04/2016.
 */
public class StringDialogFragment extends DialogFragment {
    String strToShow;

  /*  public StringDialogFragment(String strToShow) {
        this.strToShow = strToShow;
    }*/

    public void setStrToShow(String strToShow) {
        this.strToShow = strToShow;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(strToShow)
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Log.d("SuccessDialogFragment", "SuccessDialogFragment");
                    }
                });
        builder.setCancelable(true);
        return builder.create();
    }
}
