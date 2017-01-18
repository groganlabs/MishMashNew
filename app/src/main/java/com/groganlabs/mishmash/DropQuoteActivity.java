package com.groganlabs.mishmash;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class DropQuoteActivity extends GameActivity {
	DropQuoteGame mGame;
	DropQuoteView mView;
	Boolean gameChanged = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras;
		
		if((extras = getIntent().getExtras()) != null) {
			int packId = extras.getInt(PACK_ID_TAG);
			int gameId = extras.getInt(GAME_ID_TAG);
			try {
				mGame = new DropQuoteGame(gameId, packId, this);
			} catch (Exception e) {
				// TODO Need error for can't load game
				// need to create dialog, "error loading selected game.
				// Tap ok to load a random game or cancel to go back"
			}
		}
		//see if we have a saved instance with a solution, if not we need to create one
		//with the accompanying arrays 
		else if(savedInstanceState == null || savedInstanceState.getParcelable(GAME_TAG) == null) {
			try {
				mGame = new DropQuoteGame(-1, -1, this);
			} catch (Exception e1) {
				Log.d("cryptoActivity", "Error: " + e1.getMessage());
				e1.printStackTrace();
				
				noMoreGames();
			}
			
		}
		//we do have game information saved, so we'll use that
		else {
			mGame = savedInstanceState.getParcelable(GAME_TAG);
			mGame.setContext(this);
			gameChanged = savedInstanceState.getBoolean("gameChanged");
		}
		
		if(mGame != null) {
			FrameLayout layout = new FrameLayout(this);
			
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			
			mView = new DropQuoteView(this);
			mView.setLayoutParams(lp);
			layout.addView(mView);
			
			setContentView(layout);
			
			mView.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent e) {
					return gameTouch(e.getX(), e.getY());
				}
			});
		}
	}
	
	protected void gameWon() {
		android.support.v4.app.DialogFragment frag = new YouWonDialog();
		frag.show(getSupportFragmentManager(), GAME_TAG);
	}
	
	protected boolean gameTouch(float x, float y) {
		if(mView.touched(x, y)) {
			mView.invalidate();
			if(mGame.gameWon()) {
				gameWon();
			}
		}
		// Ask the view what was touched
		// Possible values - char, square or none
		// Which char or square
		// If none, remove highlights
		// If square, select the square
		//    If square has an answer, select the associated char
		// if char, and char is in same column as selected square,
		//    assign or remove answer
		// if char, and char is in different column,
		//    do nothing
		//Question is - do that here or let the view handle it all?
		//View would return if we changed an answer, then we'd check to see if the game was won
		return false;
	}

	public DropQuoteGame getGame() {
		return mGame;
	}

	@Override
	public void onRestartClick(android.support.v4.app.DialogFragment dialog) {
		mGame.clearAnswer();
		mView.setHighlight(-1);
		mView.invalidate();
	}

	@Override
	public void onNewGameSave(android.support.v4.app.DialogFragment dialog) {
		mGame.saveGame();
		startNewGame();
	}

	@Override
	public void onNewGameNoSave(android.support.v4.app.DialogFragment dialog) {
		startNewGame();
	}

	@Override
	public void onMainMenuSave(android.support.v4.app.DialogFragment dialog) {
		mGame.saveGame();
		finish();
	}

	@Override
	public void onMainMenuNoSave(android.support.v4.app.DialogFragment dialog) {
		finish();
	}

	@Override
	public void startNewGame() {
		try {
			mGame = new DropQuoteGame(-1, -1, this);
		} catch (Exception e) {
			noMoreGames();
		}
		gameChanged = false;
		mView.invalidate();
	}

	@Override
	public void showHint() {
		int hint = mGame.getHint();
		mView.setHighlight(hint);
		mView.invalidate();
		if(mGame.gameWon())
			gameWon();
	}

	@Override
	protected void menuNewGame() {
		if(!mGame.gameWon() && gameChanged) {
			android.support.v4.app.DialogFragment frag = new NewGameFromMenu();
			frag.show(getSupportFragmentManager(), GAME_TAG);
		}
		else
			startNewGame();
	}

	@Override
	protected void goToMainMenu() {
		finish();
	}

	
	
	
}
