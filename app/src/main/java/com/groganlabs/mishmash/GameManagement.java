package com.groganlabs.mishmash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;

public class GameManagement extends FragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.game_management);
		
		PackFragment packFrag = new PackFragment();
		FragmentManager fragMan = getSupportFragmentManager();
		fragMan.beginTransaction().add(R.id.game_management, packFrag).commit();
		
	}
	
	//method to deal with locked packs tap -
	// open confirmation dialog
	
	//method to deal with "yes" on above dialog
	// use GooglePlay to make the purchase
	
	//method to deal with open packs tap -
	// replace the fragment with the game list
	
	private class PackFragment extends Fragment {
		//convert to ListFragment?
		//get a list of packs
		//foreach pack, display the appropriate view
		//setup listeners for each type that send 
		// necessary info to activity
	}
	
	private class GameFragment extends ListFragment {
		//get cursor for games in selected pack
		// which will be passed in on creation
		//register listener to call appropriate methods
	}
}


