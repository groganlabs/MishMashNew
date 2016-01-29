package com.groganlabs.mishmash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class GoToMainMenuDialog extends DialogFragment {
	public interface MainMenuListener {
		public void onMainMenuSave(DialogFragment dialog);
		public void onMainMenuNoSave(DialogFragment dialog);
	}
	
	MainMenuListener mListener;

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to save your current game?")
               .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onMainMenuSave(GoToMainMenuDialog.this);
                   }
               })
               .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            	   public void onClick(DialogInterface dialog, int id) {
            		   //do nothing
            	   }
               })
               .setNegativeButton("Don't Save", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onMainMenuNoSave(GoToMainMenuDialog.this);
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (MainMenuListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
