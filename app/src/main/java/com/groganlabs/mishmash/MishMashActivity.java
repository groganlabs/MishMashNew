package com.groganlabs.mishmash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MishMashActivity extends Activity implements OnClickListener {
	
	TextView cryptogram;
	TextView dropQuotes;
	TextView jumble;
	TextView settings;
	
	IabHelper mHelper;
	Boolean helperSuccess;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Part of the in-app billing stuff
        String apiKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjyuCKHP8kRzC5uwfzanHTcDY25k5c98u2KiByFhFZDiaauxICsffOy9Ijpj8glj+VaVf261TvdkIkuqDEXBqRegrF2yDlvgZfceNINqL0EMJsJdIFSGiXXnirWEE3A4j6LT0HOjSif1UBDPXalnC+/CTc1C4QyBxTRJUpzERuEfQ34XtNaCJ6d9biH3XSiS2PRa87bdaTG3Dc5LaSqY+mtYHT3J2lP0FgbTQSYkmIJ7kG6iskcSZn/LsFAY4ZGTrCQE99SCDYiA8MQBk/oWZ7EcnEmDIYflWXsnS5TIbtV7Wz18QlsvBmNHryfw91SC7TqB8Bd/YP6Pqm0iX7ZhXUwIDAQAB";
        mHelper = new IabHelper(this, apiKey);
        // only for dev, change to false for production
        mHelper.enableDebugLogging(true, "mHelper");
        
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
        	public void onIabSetupFinished(IabResult result) {
        		helperSuccess = result.isSuccess();
        	    if(!helperSuccess) {
        	    	return;
        	    }
        	    
        	    // just in case it was disposed of while we waited
        	    if(mHelper == null) {
        	    	return;
        	    }
        	    
        	    
        	}
        });
        
        //get inventory purchased by player
        
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
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}
}