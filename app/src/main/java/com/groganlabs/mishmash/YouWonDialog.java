package com.groganlabs.mishmash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class YouWonDialog extends DialogFragment {
	public interface YouWonListener {
		public void onYouWonNewGameClick(DialogFragment dialog);
        public void onYouWonMenuClick(DialogFragment dialog);
	}

	YouWonListener mListener;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You won!!!")
               .setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onYouWonNewGameClick(YouWonDialog.this);
                   }
               })
               .setNeutralButton("Yay!", new DialogInterface.OnClickListener() {
				
            	   public void onClick(DialogInterface dialog, int which) {
            		  // do nothing? Let's try that.
					
            	   }
               })
               .setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onYouWonMenuClick(YouWonDialog.this);
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
            mListener = (YouWonListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
