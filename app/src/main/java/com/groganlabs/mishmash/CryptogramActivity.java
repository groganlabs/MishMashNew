package com.groganlabs.mishmash;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class CryptogramActivity extends GameActivity {
	private CryptogramGame mGame;
	private CryptogramView mView;
	private boolean gameChanged = false;
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras;
		//if this was created with extras in the intent
		if((extras = getIntent().getExtras()) != null) {
			int packId = extras.getInt(PACK_ID_TAG);
			int gameId = extras.getInt(GAME_ID_TAG);
			try {
				mGame = new CryptogramGame(gameId, packId, this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// need to create dialog, "error loading selected game.
				// Tap ok to load a random game or cancel to go back"
			}
		}
		//see if we have a saved instance with a solution, if not we need to create one
		//with the accompanying arrays 
		else if(savedInstanceState == null || savedInstanceState.getParcelable(GAME_TAG) == null) {
			try {
				mGame = new CryptogramGame(-1, -1, this);
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
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			
			mView = new CryptogramView(this);
			mView.setLayoutParams(lp);
			layout.addView(mView);
			
			AlphaView alpha = new AlphaView(this);
			alpha.setLayoutParams(lp);
			layout.addView(alpha);
			
			setContentView(layout);
			
			mView.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent e) {
					return gameTouch(e.getX(), e.getY());
				}
			});
			
			alpha.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					//result of the keyboard touch
					char res = ((AlphaView) v).getLetter(event.getX(), event.getY());
					int curChar;
					
					//if we have a highlighted letter
					if((curChar = mView.getHighlight()) >= 0) {
						if(res != '<' && res != '>') {
							mGame.setAnswer(res, curChar);
						}
						
						//if we aren't deleting
						if(res != 0 && res != '<' && res != '>') {
							curChar++;
							if(curChar == mGame.getSolutionArr().length)
								curChar = 0;
							gameChanged = true;
							//advance the highlight to the next word character
							while(!isWordChar(mGame.getSolutionArr(), curChar)) {
								curChar++;
								if(curChar == mGame.getSolutionArr().length)
									curChar = 0;
							}
							
							mView.setHighlight(curChar);
						}
						else if(res == '>') {
							curChar++;
							if(curChar == mGame.getSolutionArr().length)
								curChar = 0;
							while(!isWordChar(mGame.getSolutionArr(), curChar)) {
								curChar++;
								if(curChar == mGame.getSolutionArr().length)
									curChar = 0;
							}
							
							mView.setHighlight(curChar);
						}
						else if(res == '<') {
							curChar--;
							if(curChar < 0)
								curChar = mGame.getSolutionArr().length-1;
							while(!isWordChar(mGame.getSolutionArr(), curChar)) {
								curChar--;
								if(curChar < 0)
									curChar = mGame.getSolutionArr().length-1;
							}
							
							mView.setHighlight(curChar);
						}
						mView.invalidate();
						if(mGame.gameWon()) {
							Log.d("jumble", "You win!");
							gameWon();
						}
					}
					//If no letter is selected, do nothing
					
					return false;
				}
			});
		}
	}

	protected void gameWon() {
		DialogFragment frag = new YouWonDialog();
		frag.show(getSupportFragmentManager(), GAME_TAG);
	}

	protected boolean isWordChar(char[] charArray, int ii) {
		if(ii >= charArray.length || ii < 0) {
			return false;
		}
		if((charArray[ii] >= 'A' && charArray[ii] <= 'Z')) {
			return true;
		}
		else
			return false;
	}

	protected boolean gameTouch(float x, float y) {
		//If no letter touched, and a letter was highlighted
		//Tell the view to remove the highlight and draw
		//Else
		//Get the letter selected - index of the array
		//Have the view highlight that character and draw
		int touched = mView.getTouched(x, y);
		int newC;
		
		//no game letter was touched
		if(touched < 0) {
			newC = -1;
		}
		//should never happen, but just in case
		else if(touched >= mGame.getSolution().length()) {
			newC = -1;
		}
		//the user touched a spot within the game
		else {
			if(mGame.getSolutionArr()[touched] >= 'A' && mGame.getSolutionArr()[touched] <= 'Z')
				newC = touched;
			else
				newC = -1;
		}
		
		//We only need to redraw if the highlighting is changing
		if(mView.setHighlight(newC))
			mView.invalidate();
		
		return false;
	}

	@Override
	public void onRestartClick(DialogFragment dialog) {
		mGame.clearAnswer();
		mView.setHighlight(-1);
		mView.invalidate();
	}

	@Override
	public void onNewGameSave(DialogFragment dialog) {
		mGame.saveGame();
		startNewGame();
	}

	public void onNewGameNoSave(DialogFragment dialog) {
		startNewGame();
		
	}

	public void onMainMenuSave(DialogFragment dialog) {
		mGame.saveGame();
		finish();
	}

	public void onMainMenuNoSave(DialogFragment dialog) {
		finish();
		
	}

	@Override
	public void startNewGame() {
		// Create a new game
		try {
			mGame = new CryptogramGame(-1, -1, this);
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
			DialogFragment frag = new NewGameFromMenu();
			frag.show(getSupportFragmentManager(), GAME_TAG);
		}
		else
			startNewGame();
	}

	@Override
	protected void goToMainMenu() {
		/*if(!mGame.gameWon() && gameChanged) {
			DialogFragment frag = new GoToMainMenuDialog();
			frag.show(getSupportFragmentManager(), GAME_TAG);
		}
		else*/
		finish();
	}

	public CryptogramGame getGame() {
		return mGame;
	}
}
