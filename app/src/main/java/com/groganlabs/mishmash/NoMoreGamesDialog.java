package com.groganlabs.mishmash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class NoMoreGamesDialog extends DialogFragment {
	public interface NoMoreGamesListener {
		public void onNoMorePositiveClick(DialogFragment dialog);
		public void onNoMoreNeutralClick(DialogFragment dialog);
        public void onNoMoreNegativeClick(DialogFragment dialog);
        public void onNoMoreDismiss(DialogFragment dialog);
	}
	
	NoMoreGamesListener mListener;
	private String message = "To continue playing, you can\n" +
			"Select \"reuse games\" or \"replay games\" in the settings page\n"; /* +
			"Finish any games in progress\n" +
			"Buy another game pack.";*/
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("No more games available!")
        	   .setMessage(message)
               .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onNoMorePositiveClick(NoMoreGamesDialog.this);
                   }
               })
               /*.setNeutralButton("Buy Pack", new DialogInterface.OnClickListener() {
            	   public void onClick(DialogInterface dialog, int which) {
            		  mListener.onNoMoreNeutralClick(NoMoreGamesDialog.this);
            	   }
               })*/
               .setNegativeButton("Menu", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onNoMoreNegativeClick(NoMoreGamesDialog.this);
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
            mListener = (NoMoreGamesListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoMoreGamesListener");
        }
    }
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		mListener.onNoMoreDismiss(null);
	}
}
