package com.groganlabs.mishmash;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class JumbleView extends View {
	//Paint object for displaying text
	private Paint mTextPaint;
	
	//Paint object for highlighting active text
	private Paint mBgPaint;
	
	private Context mContext;
	
	//width of the View
	private int mW;
	
	//mCharSize is the final size of the printed characters
	//mFontSize is an intermediately calculated size for the characters
	private float mCharSize, mFontSize;
	
	//Padding between rows
	private float mYPad = 20f;
	
	//each element is the padding on one side of a row
	private int[] mXPad;
	
	//number of rows the game is broken into
	private int mNumRows;
	
	//index of the "selected" character
	private int mHighlighted = -1;
	
	//array of indices for the game/answer arrays to indicate
	//where the line breaks are
	//mRowIndices[0] = 0 (first row start), [1] = second row start, etc
	private int[] mRowIndices;
	
	/**
	 * constructor used to create view from code
	 * @param context
	 */
	public JumbleView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	/**
	 * Constructor used to create view from xml
	 * @param context
	 * @param attrs
	 */
	public JumbleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public float getCharSize() {
		return mCharSize;
	}
	
	public int getNumRows() {
		return mNumRows;
	}
	
	public float getYPad() {
		return mYPad;
	}
	
	public int getXPad(int ii) {
		if(ii < mNumRows) {
			return mXPad[ii];
		}
		else {
			return -1;
		}
	}
	
	/**
	 * create Paint objects
	 */
	private void init() {
		//We want the characters to be a minimum size
		mFontSize = 18 * mContext.getResources().getDisplayMetrics().density;
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(getResources().getColor(R.color.text));
		
		mBgPaint = new Paint();
		//mBgPaint.setColor(0xff00ffff);
		//mBgPaint.setColor(Color.BLUE);
		mBgPaint.setColor(getResources().getColor(R.color.highlight));
		mBgPaint.setStyle(Paint.Style.FILL);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mW = w;
	}
	
	/**
	 * Establishing the size of the View and the characters 
	 */
	@Override
	protected void onMeasure(int wMS, int hMS) {
		int w = MeasureSpec.getSize(wMS);
		int h = MeasureSpec.getSize(hMS);
		
		//determine char size by either maximum characters per row
		//or by minimum size calculated by screen density (mFontSize)
		//Actually, let's just use calc by screen density for now
		/*float maxCharSize = w / 30f;
		
		if(maxCharSize >= mFontSize) {
			mCharSize = maxCharSize;
		}
		else {*/
			mCharSize = mFontSize;
			mTextPaint.setTextSize(mCharSize);
			
		//}
		
		// Game takes up all available space except what the keyboard needs
		int alphaHeight = AlphaView.getHeight(w, (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT));
		setMeasuredDimension(w, h-alphaHeight);
	}
	
	/**
	 * Takes the x and y coordinates of a touch event and returns 
	 * the index of the letter touched, or -1 if no letter.
	 * @param x
	 * @param y
	 * @return the index of the letter touched, or -1
	 */
	public int getTouched(float x, float y) {
		int row = (int) Math.floor(y / (mCharSize * 2 + mYPad));
		int retChar = 0, col;
		//touch registered below the game
		if(row >= mNumRows) {
			retChar = -1;
		}
		else {
			//see if x is outside the printed row
			if(x < mXPad[row] || x > mW - mXPad[row]) {
				retChar = -1;
			}
			else {
				col = (int) Math.floor((x - mXPad[row])/(mCharSize));
				retChar += mRowIndices[row];
				retChar += col;
			}
		}
		return retChar;
	}

	/**
	 * Set mHighlighted to the passed in value.
	 * If the value is different, return true,
	 * else return false to indicate no change 
	 * @param h
	 * @return
	 */
	public boolean setHighlight(int h) {
		if(mHighlighted == h) {
			return false;
		}
		else {
			mHighlighted = h;
			return true;
		}
	}
	
	public int getHighlight() {
		return mHighlighted;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//var used to determine line breaks
		int maxCharsPerRow = (int) ((int) mW / mCharSize);
		
		// var used to set max size of row based arrays
		// TODO: set this based off of the size of the screen.
		// could be moved to onMeasure?
		int maxRows = 10;
		
		// We want to get the required game elements each time we draw
		JumbleGame game = ( (JumbleActivity) mContext).getGame();
		String gameStr = game.getSolution();
		char[] gameArray = game.getPuzzleArr();
		char[] answerArray = game.getAnswerArr();
		//Log.d("jumbleView", "puzzle: " + String.valueOf(gameArray));
		int ii, lastSpace = 0, rowSize = 0;
		mNumRows = 0;
		
		//mRowIndices indicates where each row starts (1st row at 0, second at mRowIndices[1], etc)
		mRowIndices = new int[maxRows];
		mRowIndices[0] = 0;
		
		// Loop through and find the line breaks - maybe move this to onMeasure?
		// also determine how many lines we actually have
		while(mNumRows < maxRows) {
			if(mRowIndices[mNumRows] + maxCharsPerRow >= gameArray.length) {
				mNumRows++;
				break;
			}
			
			lastSpace = gameStr.lastIndexOf(' ', mRowIndices[mNumRows] + maxCharsPerRow);
			mNumRows++;
			// TODO: insert a - into the arrays & string in case of hyphenation?
			if(lastSpace == -1) {
				mRowIndices[mNumRows] = mRowIndices[mNumRows-1] + maxCharsPerRow;
			}
			else {
				mRowIndices[mNumRows] = lastSpace;
			}
		}
		
		mXPad = new int[mNumRows];
		String answer;
		int jj;
		
		float aCharWidth, gCharWidth, aCharPadding, gCharPadding;
		
		//drawing the game, line by line
		//Starting by finding the margin for the row
		for(ii = 0; ii < mNumRows; ii++) {
			//last row
			if(ii == mNumRows - 1) {
				//rowSize = gameArray.length - mRowIndices[ii] - 1;
				rowSize = gameArray.length - mRowIndices[ii];
				mXPad[ii] = (int) ((mW - (rowSize * mCharSize))/2);
			}
			//first row
			else if(ii == 0) {
				rowSize = mRowIndices[ii+1] - mRowIndices[ii];
				mXPad[ii] = (int) ((mW - (rowSize * mCharSize))/2);
			}
			//middle rows
			else {
				//rowSize = mRowIndices[ii+1] - mRowIndices[ii] - 1;
				rowSize = mRowIndices[ii+1] - mRowIndices[ii];
				mXPad[ii] = (int) ((mW - (rowSize * mCharSize))/2 - mCharSize);
			}
			///Log.d("jumbleView", "RowSize: " + rowSize);
			
			//drawing the characters for the line
			for(jj = 0; jj < rowSize; jj++) {
				if(ii == 0 && jj == rowSize) {
					continue;
				}
				
				//don't need to do anything for spaces
				if(gameArray[mRowIndices[ii]+jj] != ' ') {
					//find out if we have an answer or a blank
					if(answerArray[mRowIndices[ii]+jj] == 0 || answerArray[mRowIndices[ii]+jj] == ' ') {
						answer = "_";
					}
					else {
						answer = String.valueOf(answerArray[mRowIndices[ii]+jj]);
					}
					
					//code for centering the individual letters
					aCharWidth = mTextPaint.measureText(answer);
					gCharWidth = mTextPaint.measureText(gameArray, mRowIndices[ii]+jj, 1);
					aCharPadding = (mCharSize-aCharWidth)/2f;
					gCharPadding = (mCharSize-gCharWidth)/2f;
					
					//highlighting the selected letter
					if(mRowIndices[ii]+jj == mHighlighted) {
						/*Log.d("jumbleView", "This should be highlighted");
						Log.d("jumbleView", "Left: "+(mXPad[ii]+(mCharSize*jj)));
						Log.d("jumbleView", "Top: "+(mCharSize*(2f*ii+1)+(mYPad*ii)-mCharSize));
						Log.d("jumbleView", "Right: "+(mXPad[ii]+(mCharSize*jj) - mCharSize));
						Log.d("jumbleView", "Bottom: "+(mCharSize*(2f*ii+1)+(mYPad*ii)));
						Log.d("jumbleView", "Bottom pt 1: "+(mCharSize*(2f*ii+1)));
						Log.d("jumbleView", "Bottom pt 2: "+(mYPad*ii));
						Log.d("jumbleView", "Bottom pt 3: "+(mCharSize));*/
						
						canvas.drawRect(mXPad[ii]+(mCharSize*jj), 
								mCharSize*(2f*ii+1)+(mYPad*ii)-mCharSize, 
								mXPad[ii]+(mCharSize*jj) + mCharSize, 
								mCharSize*(2f*ii+1)+(mYPad*ii), 
								mBgPaint);
					}
					
					canvas.drawText(answer, 
							mXPad[ii]+(mCharSize*jj)+aCharPadding, 
							mCharSize*(2f*ii+1)+(mYPad*ii), 
							mTextPaint);
					canvas.drawText(gameArray, 
							mRowIndices[ii]+jj, 1, 
							mXPad[ii]+(mCharSize*jj)+gCharPadding, 
							mCharSize*(2f*ii+2)+(mYPad*ii), 
							mTextPaint);
				}
			}
		}
	}
}
