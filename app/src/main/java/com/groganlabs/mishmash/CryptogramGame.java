package com.groganlabs.mishmash;

import java.util.Random;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CryptogramGame extends Game {

	protected int gameType = CRYPTO_GAME;
	
	protected char[] key;
	
	public CryptogramGame(int game, int pack, Context context) throws Exception {
		super(game, pack, context);
	}
	
	public CryptogramGame(Parcel parcel) {
		super(parcel);
	}

	@Override
	public int describeContents() {
		
		return 0;
	}

	@Override
	protected void createGame() {
		Log.d("crypto", "Creating game");
		Random rand = new Random();
		key = new char[26];
		
		// index represents the nth available index
		// in the key array
		int index;
		int counter = 0;
		
		// create "key" array
		// Key array - indices 0-25, values 'A' - 'Z'
		//    indices represent 'A' - 'Z' by adding 'A'
		// ii = letters added to the key array
		for(int ii = 25; ii >= 0; ii--) {
			index = rand.nextInt(ii + 1);
			counter = 0;
			// jj = key index
			for(int jj = 0; jj < 26; jj++) {
				if(counter == index && key[jj] == 0) {
					key[jj] = (char) (ii + 'A');
					if(jj == ii && index != 0) {
						int newIndex = (jj + index) % 26;
						char oldValue = key[newIndex];
						key[jj] = oldValue;
						key[newIndex] = (char) (ii + 'A');
					} // if we're putting the same letter and index != 0
					else if(jj == ii && index == 0) {
						// keep getting a new index until it isn't 0
						while(true) {
							index = rand.nextInt(26);
							// then swap our current letter with another slot
							if(index != 0) {
								int newIndex = (jj + index) % 26;
								char oldValue = key[newIndex];
								key[jj] = oldValue;
								key[newIndex] = (char) (ii + 'A');
								break;
							}
						} // while
					} // else we're putting the same and index = 0
					break;
				}// if we're at the spot we want to put the current letter in
				
				if(key[jj] == 0)
					counter++;
				
			} // end internal loop looking for the spot to place the letter
			
		} // end loop counting down from 'Z' to 'A'
		
		// convert solution into puzzle
		for(int ii = 0; ii < solutionArr.length; ii++) {
			if(solutionArr[ii] >= 'A' && solutionArr[ii] <= 'Z')
				puzzleArr[ii] = key[solutionArr[ii] - 'A'];
			else {
				puzzleArr[ii] = solutionArr[ii];
				answerArr[ii] = solutionArr[ii];
			}
		}
	}

	@Override
	public void clearAnswer() {
		for(int ii = 0; ii < solutionArr.length; ii++) {
			if(solutionArr[ii] < 'A' || solutionArr[ii] > 'Z')
				answerArr[ii] = solutionArr[ii];
			else
				answerArr[ii] = 0;
		}
	}
	
	/**
	 * When one character is assigned an answer,
	 * all other instances of that character get
	 * the same answer
	 * @param a The answer given
	 * @param index The location of the answer
	 */
	public void setAnswer(char a, int index) {
		char answeredChar = puzzleArr[index];
		if(answeredChar < 'A' || answeredChar > 'Z')
			return;
		
		for(int ii = 0; ii < puzzleArr.length; ii++) {
			if(puzzleArr[ii] == answeredChar) {
				answerArr[ii] = a;
			}
		}
	}
	
	public int getGameType() {
    	return CRYPTO_GAME;
    }

	public static final Parcelable.Creator<CryptogramGame> CREATOR = new Parcelable.Creator<CryptogramGame>() {
        public CryptogramGame createFromParcel(Parcel in) {
            return new CryptogramGame(in);
        }
        public CryptogramGame[] newArray(int size) {
            return new CryptogramGame[size];
        }
    };
}
