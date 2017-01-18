package com.groganlabs.mishmash;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.groganlabs.mishmash.GoToMainMenuDialog.MainMenuListener;
import com.groganlabs.mishmash.NewGameFromMenu.NewGameListener;
import com.groganlabs.mishmash.NoMoreGamesDialog.NoMoreGamesListener;
import com.groganlabs.mishmash.RestartDialog.RestartListener;
import com.groganlabs.mishmash.YouWonDialog.YouWonListener;

public abstract class GameActivity extends AppCompatActivity implements
		NoMoreGamesListener, YouWonListener, RestartListener, NewGameListener, MainMenuListener  {
	
	public static String GAME_TAG = "game";
	public static String PACK_ID_TAG = "packId";
	public static String GAME_ID_TAG = "gameId";
	
	/**
	 * Generates the dialog if there are no games to build
	 */
	protected void noMoreGames() {
		DialogFragment frag = new NoMoreGamesDialog();
		frag.show(getSupportFragmentManager(), GAME_TAG);
	}

	/**
	 * After a game is won, start a new one
	 */
	public void onYouWonNewGameClick(DialogFragment dialog) {
		startNewGame();
	}

	/**
	 * After a game is won, go to the menu
	 */
	public void onYouWonMenuClick(DialogFragment dialog) {
		finish();
	}

	/**
	 * Handles the action after the first button of the
	 * NoMoreGamesFragment has been clicked.
	 * For now, that means opening the settings activity
	 */
	public void onNoMorePositiveClick(DialogFragment dialog) {
		Intent intent = new Intent(this, MishMashSettings.class);
		startActivity(intent);
	}

	/**
	 * Handles the action after the third button of the
	 * NoMoreGamesFragment has been clicked.
	 * For now, that means going back to the menu.
	 */
	public void onNoMoreNegativeClick(DialogFragment dialog) {
		finish();
	}

	/**
	 * If the user taps back or away from the dialog,
	 * let's take them back to the menu instead of leaving
	 * them with a blank screen
	 */
	public void onNoMoreDismiss(DialogFragment dialog) {
		finish();
	}

	/**
	 * Handles the action after the second button of the
	 * NoMoreGamesFragment has been clicked.
	 * For now, that means going to game management.
	 */
	public void onNoMoreNeutralClick(DialogFragment dialog) {
		// TODO: start the game management activity
	}
	
	/**
	 * Create a new game and refresh the screen.
	 */
	abstract public void startNewGame();
	
	/**
	 * Show "do you want to start over?" dialog,
	 * if yes then use game.clearAnswer();
	 */
	public void restartGame() {
		Log.d("game", "showing dialog");
		DialogFragment frag = new RestartDialog();
		frag.show(getSupportFragmentManager(), GAME_TAG);
	}
	
	abstract public void showHint();
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_restart:
			restartGame();
			return true;
		case R.id.menu_hint:
			showHint();
			return true;
		case R.id.menu_main:
			goToMainMenu();
			return true;
		case R.id.menu_new_game:
			//menuNewGame();
			startNewGame();
			return true;
		case R.id.menu_settings:
			Intent intent = new Intent(this, MishMashSettings.class);
			startActivity(intent);
			return true;
		}
		
		return false;
		
	}
	
	abstract protected void menuNewGame();
	
	
	abstract protected void goToMainMenu();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.gamemenu, menu);
	    return true;
	}
}
