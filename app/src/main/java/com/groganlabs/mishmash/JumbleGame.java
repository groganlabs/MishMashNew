package com.groganlabs.mishmash;

import java.util.Random;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class JumbleGame extends Game {
	protected int gameType = JUMBLE_GAME;

	public JumbleGame(int game, int pack, Context context) throws Exception {
		super(game, pack, context);
		//Log.d("jumbleGame", "puzzle: "+String.valueOf(puzzleArr));
	}
	
	public JumbleGame(Parcel in) {
		super(in);
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void createGame() {
		int start = 0;
		int end;
		for(int ii = 0; ii < solutionArr.length; ii++) {
			// any non-word character
			// For a -, the char before should be ' ' otherwise it's in a word
			if(((solutionArr[ii] < 'A' || solutionArr[ii] > 'Z') && solutionArr[ii] != '\'') || (solutionArr[ii] == '-' && solutionArr[ii-1] == ' ')) {
				
				puzzleArr[ii] = solutionArr[ii];
				answerArr[ii] = solutionArr[ii];
				if(start == ii) {
					start++;
				}
				else {
					end = ii - 1;
					mixupWord(start, end, puzzleArr, solutionArr);
					start = ii+1;
				}
			}
		}
		
		/*for(int ii = 0; ii < solutionArr.length; ii++) {
			if(solutionArr[ii] < 'A' || solutionArr[ii] > 'Z') {
				answerArr[ii] = solutionArr[ii];
			}
		}*/

	}
	
	/**
	 * Randomly mixes up the order of a word located in solution from
	 * solution[start] to solution[end] inclusive and copies it to
	 * the game array in the same indices.
	 * @param start index of the first letter of the word
	 * @param end index of the last letter of the word
	 * @param game array the mixed up word is copied to
	 * @param solution source of the word
	 */
	public void mixupWord(int start, int end, char[] game, char[] solution) {
		//doesn't make sense, let's just get out
		if(start > end ) 
			return;
		//one letter word, no mixing needed
		else if(start == end) {
			game[start] = solution[start];
			return;
		}
		
		Random rand = new Random();
		int length = end - start + 1;
		int[] used = new int[length];
		int newLetter;
		
		for(int ii = 0; ii <length; ii ++) {
			newLetter = rand.nextInt(length);
			while(used[newLetter] == 1) {
				newLetter++;
				if(newLetter == length) {
					newLetter = 0;
				}
			}
			used[newLetter] = 1;
			game[start + ii] = solution[start + newLetter];
		}
	}

	public static final Parcelable.Creator<JumbleGame> CREATOR = new Parcelable.Creator<JumbleGame>() {
        public JumbleGame createFromParcel(Parcel in) {
            return new JumbleGame(in);
        }
        public JumbleGame[] newArray(int size) {
            return new JumbleGame[size];
        }
    };
    
    public int getGameType() {
    	return JUMBLE_GAME;
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
}
