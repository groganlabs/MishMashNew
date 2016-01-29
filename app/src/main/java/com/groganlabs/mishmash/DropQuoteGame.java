package com.groganlabs.mishmash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;

public class DropQuoteGame extends Game {
	
	protected char[] dqSolution;
	protected int dqSolutionLength;
	
	// 2d array of letters for the top of the puzzle
	// contains DqChar objects
	protected ArrayList<ArrayList<DqChar>> letterCols;
	
	public DropQuoteGame(int game, int pack, Context context) throws Exception {
		super(game, pack, context);
		
	}
	
	public DropQuoteGame(Parcel in) {
		super(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected void createGame() {
		int counter = 0;
		dqSolution = new char[solutionArr.length];
		
		for(int ii = 0; ii < solutionArr.length; ii++) {
			if((solutionArr[ii] >= 'A' && solutionArr[ii] <= 'Z') || solutionArr[ii] == ' ') {
				dqSolution[counter] = solutionArr[ii];
				counter++;
			}
			
			if(solutionArr[ii] == ' ') {
				answerArr[counter - 1] = ' ';
			}
		}
		
		dqSolutionLength = counter;
		
	}

	@Override
	public void clearAnswer() {
		for(int ii = 0; ii < dqSolutionLength; ii++) {
			if(answerArr[ii] >= 'A' && answerArr[ii] <= 'Z') {
				answerArr[ii] = 0;
			}
		}
	}
	
	public void initializeLetterCols(int cols, int rows) {
		if(letterCols != null && letterCols.size() > 0) {
			return;
		}
		
		letterCols = new ArrayList<ArrayList<DqChar>>();
		for(int ii = 0; ii < cols; ii++) {
			letterCols.add(new ArrayList<DqChar>(rows));
		}
		
		for(int ii=0; ii < dqSolutionLength; ii++) {
			if(dqSolution[ii] != ' ') {
				((ArrayList) letterCols.get(ii%cols)).add(new DqChar(dqSolution[ii]));
			}
		}
		
		// Shuffle the columns
		Random random = new Random();
		for(int ii = 0; ii < cols; ii++) {
			Collections.shuffle((ArrayList<DqChar>) letterCols.get(ii), random);
		}
		
	}
	
	public int getGameType() {
		return DROP_GAME;
	}

	public int getLength() {
		return dqSolutionLength;
	}
	
	@Override
	public int getHint() {
		//count how many indices are blank or have a wrong answer
		int numAvail = 0, lastAvail = 0;
		for(int ii = 0; ii < dqSolutionLength; ii++) {
			if(answerArr[ii] != dqSolution[ii]) {
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
			answerArr[lastAvail] = dqSolution[lastAvail];
			return lastAvail;
		}
		
		//get a random number 
		Random rand = new Random();
		int hint = rand.nextInt(numAvail);
		Log.d("game", "hint: "+hint);
		int jj = -1;
		//Go back through the arrays, counting through the
		//available indices until we get to the hint'th mismatching element
		for(int ii = 0; ii < answerArr.length; ii++) {
			if(answerArr[ii] != dqSolution[ii]) {
				jj++;
			}

			if(jj == hint) {
				answerArr[ii] = dqSolution[ii];
				return ii;
			}
			
		}
		return -1;
	}

	@Override
	public Boolean gameWon() {
		if(Arrays.equals(dqSolution, answerArr)) {
			finishGame();
			return true;
		}
		else {
			return false;
		}
	}
}
