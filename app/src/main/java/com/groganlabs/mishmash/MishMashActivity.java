package com.groganlabs.mishmash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.groganlabs.mishmash.util.IabHelper;
import com.groganlabs.mishmash.util.IabResult;
import com.groganlabs.mishmash.util.IabBroadcastReceiver.IabBroadcastListener;
import com.groganlabs.mishmash.util.IabException;
import com.groganlabs.mishmash.util.Inventory;

public class MishMashActivity extends Activity implements IabBroadcastListener, OnClickListener {
	
	TextView cryptogram;
	TextView dropQuotes;
	TextView jumble;
	TextView settings;
	
	Boolean helperSuccess;

	IabHelper mHelper;
	IInAppBillingService mService;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Old billing methods
        String apiKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjyuCKHP8kRzC5uwfzanHTcDY25k5c98u2KiByFhFZDiaauxICsffOy9Ijpj8glj+VaVf261TvdkIkuqDEXBqRegrF2yDlvgZfceNINqL0EMJsJdIFSGiXXnirWEE3A4j6LT0HOjSif1UBDPXalnC+/CTc1C4QyBxTRJUpzERuEfQ34XtNaCJ6d9biH3XSiS2PRa87bdaTG3Dc5LaSqY+mtYHT3J2lP0FgbTQSYkmIJ7kG6iskcSZn/LsFAY4ZGTrCQE99SCDYiA8MQBk/oWZ7EcnEmDIYflWXsnS5TIbtV7Wz18QlsvBmNHryfw91SC7TqB8Bd/YP6Pqm0iX7ZhXUwIDAQAB";
        mHelper = new IabHelper(this, apiKey);
        // only for dev, change to false for production
        mHelper.enableDebugLogging(true, "mHelper");
        
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
        	public void onIabSetupFinished(IabResult result) {
        		helperSuccess = result.isSuccess();
        	    if(!helperSuccess) {
					Log.d("mHelper", "Problem setting up in app billing: " + result);
        	    	return;
        	    }
        	    
        	    /*// just in case it was disposed of while we waited
        	    if(mHelper == null) {
        	    	return;
        	    }*/
        	}
        });

        //get inventory purchased by player
		try {
			mHelper.queryInventoryAsync(mGotInventoryListener);
		} catch (IabHelper.IabAsyncInProgressException e) {
			Log.d("billing", "Problem getting purchases");
		} catch (Exception e) {
			Log.d("billing", "a different problem");
		}


        //make sure database is up to date
        
        cryptogram = (TextView) findViewById(R.id.cryptoBtn);
        dropQuotes = (TextView) findViewById(R.id.dropBtn);
        jumble = (TextView) findViewById(R.id.jumbleBtn);
        settings = (TextView) findViewById(R.id.settingsBtn);
        
        cryptogram.setOnClickListener(this);
        dropQuotes.setOnClickListener(this);
        jumble.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

	public void onClick(View v) {
		Intent i;
		if(v.getId() == R.id.cryptoBtn) {
			i = new Intent(this, CryptogramActivity.class);
			startActivity(i);
		}
		else if(v.getId() == R.id.dropBtn) {
			i = new Intent(this, DropQuoteActivity.class);
			startActivity(i);
		}
		else if(v.getId() == R.id.jumbleBtn) {
			i = new Intent(this, JumbleActivity.class);
			startActivity(i);
		}
		else if(v.getId() == R.id.settingsBtn) {
			i = new Intent(this, MishMashSettings.class);
			startActivity(i);
		}
	}

	@Override
	public void receivedBroadcast() {
		// Received a broadcast notification that the inventory of items has changed
		Log.d("billing", "Received broadcast notification. Querying inventory.");
		try {
			mHelper.queryInventoryAsync(mGotInventoryListener);
		} catch (IabHelper.IabAsyncInProgressException e) {
			Log.d("billing", "Error querying inventory. Another async operation in progress.");
		}
	}

	// Listener that's called when we finish querying the items and subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inv) {
			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null) return;

			if (result.isFailure()) {
				Log.d("billing", "Failed to get purchased items");
				return;
			}
			else {
				// does the user have the premium upgrade?
				// Purchase pack = inv.getPurchase(pack_sku);
				// if(pack != null) player does have the pack
				//mIsPremium = inventory.hasPurchase(SKU_PREMIUM);
				// update UI accordingly
			}
		}
	};

	/*
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}*/
}