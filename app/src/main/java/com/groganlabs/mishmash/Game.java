package com.groganlabs.mishmash;

import java.util.Arrays;
import java.util.Random;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public abstract class Game implements Parcelable {
	public static int JUMBLE_GAME = 1;
	public static int CRYPTO_GAME = 2;
	public static int DROP_GAME = 4;
	
	//ids from the db
	protected int gameId, packId;
	protected String solution;
	protected Context mContext;
	
	protected int gameType;
	// solution - the original phrase
	// answer - the player's answers
	// puzzle - the solution converted into the puzzle played
	protected char[] puzzleArr, answerArr, solutionArr;
	
	protected MishMashDB dbHelper;
	
	/**
	 * Creates a new game object. If no game or pack is specified,
	 * use -1
	 * @param game The game id, or -1
	 * @param pack The pack id, or -1
	 * @param context
	 * @throws Exception 
	 */
	public Game(int game, int pack, Context context) throws Exception {
		
		gameId = game;
		packId = pack;
		mContext = context;
		dbHelper = new MishMashDB(mContext, MishMashDB.DB_NAME, null, MishMashDB.latestVersion);
		if(!dbHelper.getGame(this)) {
			throw new Exception("No more games!");
		}
		
		solutionArr = solution.toCharArray();
		puzzleArr = new char[solutionArr.length];
		answerArr = new char[solutionArr.length];
		
		createGame();
	}

	public Game(Parcel in) {
		gameId = in.readInt();
		packId = in.readInt();
		solution = in.readString();
		in.readCharArray(answerArr);
		in.readCharArray(puzzleArr);
		in.readCharArray(solutionArr);
		dbHelper = new MishMashDB(mContext, null, null, MishMashDB.latestVersion);
		
	}
	
	/**
	 * takes the solution and converts it into the appropriate puzzle
	 */
	abstract protected void createGame();
	
	abstract public void clearAnswer();
	
	public void saveGame() {
		
	}
	
	public int getHint() {
		//count how many indices are blank or have a wrong answer
		int numAvail = 0, lastAvail = 0;
		for(int ii = 0; ii < answerArr.length; ii++) {
			if(answerArr[ii] != solutionArr[ii]) {
				numAvail++;
				lastAvail = ii;
			}
		}
		Log.d("game", "numAvail: "+numAvail);
		
		//shouldn't happen, but just in case
		if(numAvail == 0) {
			Log.d("game", "Oops!");
			return -1;
		}
		
		if(numAvail == 1) {
			answerArr[lastAvail] = solutionArr[lastAvail];
			return lastAvail;
		}
		
		//get a random number 
		Random rand = new Random();
		int hint = rand.nextInt(numAvail);
		Log.d("game", "hint: "+hint);
		int jj = -1;
		//Go back through the arrays, counting through the
		//available indices until we get to the hint'th element
		for(int ii = 0; ii < answerArr.length; ii++) {
			if(answerArr[ii] != solutionArr[ii]) {
				jj++;
			}

			if(jj == hint) {
				answerArr[ii] = solutionArr[ii];
				return ii;
			}
			
		}
		return -1;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(solution);
		dest.writeCharArray(answerArr);
		dest.writeCharArray(puzzleArr);
		dest.writeCharArray(solutionArr);
		dest.writeInt(gameId);
		dest.writeInt(packId);
	}
	
	/*
	 *duh, can't instantiate an abstract class, 
	 * will have to implement the below in each subclass
	public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };
	*/
	
	public Boolean gameWon() {
		if(Arrays.equals(solutionArr, answerArr)) {
			finishGame();
			return true;
		}
		else {
			return false;
		}
	}
	
	protected void finishGame() {
		// TODO remove game from active table if it's there
		dbHelper.removeActiveGame(this);
		dbHelper.markGameWon(this);
	}

	public char[] getSolutionArr() {
		return solutionArr;
	}
	
	public String getSolution() {
		return solution;
	}
	
	public char[] getAnswerArr() {
		return answerArr;
	}
	
	public char[] getPuzzleArr() {
		return puzzleArr;
	}
	
	public int getGameType() {
    	return gameType;
    }
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public void setAnswerChar(char a, int index) {
		answerArr[index] = a;
	}

	public void setSolution(String string) {
		solution = string;
	}
	
	public void setGameId(int id) {
		gameId = id;
	}

	public int getGameId() {
		return gameId;
	}
}
