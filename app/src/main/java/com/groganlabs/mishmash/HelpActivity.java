package com.groganlabs.mishmash;

import android.app.Activity;
import android.os.Bundle;
//import android.support.v7.internal.widget.AdapterViewCompat.OnItemClickListener;
//import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by katie_000 on 2/3/2017.
 */

public class HelpActivity extends Activity {
	String[] helps = {"Getting Started", "Cryptogram", "Drop Quote", "Jumble", "Back to Main Menu"};
	String[] layouts = {"help_start", "help_crypto", "help_drop", "help_jumble"};
	boolean helpScreen = false;
	ArrayAdapter<String> adapter;
	ListView list;
	Button back;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setupList();

	} // onCreate

	@Override
	public void onBackPressed() {
		if(helpScreen) {
			setupList();
			return;
		}
		super.onBackPressed();
	}

	public void backTap(View view) {
		setupList();
	}

	private void setupList() {
		helpScreen = false;
		setContentView(R.layout.help_list);
		list = (ListView) findViewById(R.id.helpList);
		//list = getListView();
		//set up list
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, helps);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0:
						setContentView(R.layout.help_start);
						helpScreen = true;
						break;
					case 1:
						setContentView(R.layout.help_crypto);
						helpScreen = true;
						break;
					case 2:
						setContentView(R.layout.help_drop);
						helpScreen = true;
						break;
					case 3:
						setContentView(R.layout.help_jumble);
						helpScreen = true;
						break;
					case 4:
						finish();
						break;
					default:
						break;
				}
			}
		}); // setOnItemClickListener
	}

}
