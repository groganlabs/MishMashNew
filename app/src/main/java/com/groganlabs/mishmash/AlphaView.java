package com.groganlabs.mishmash;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class AlphaView extends View {
	Context mContext;
	Boolean mPortrait;
	int mW;
	int mH;
	float mFontSize, mCharSize;
	private Paint mTextPaint;
	//private String mDelete = "<-x-]";
	private String mDelete = "_";


	/**
	 * Constructor to create view from code
	 * @param context
	 */
	public AlphaView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	/**
	 * Constructor used to create view from xml
	 * @param context
	 * @param attrs
	 */
	public AlphaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init() {
		//do I need to determine a minimum size?
		mFontSize = 18 * mContext.getResources().getDisplayMetrics().density;
		//setup Paint object
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(0xffffffff);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mW = w;
		mH = h;
	}
	
	@Override
	protected void onMeasure(int wMS, int hMS) {
		int w = MeasureSpec.getSize(wMS);
		
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
		    mPortrait = false;
		}
		else {
			mPortrait = true;
		}
		if(mPortrait) {
			mCharSize = w / 20f;
		}
		else {
			mCharSize = w / 30f;
		}
		
		int newH;
		if(mPortrait) {
			newH = (int) (mCharSize * 4 * 1.2);
		}
		else {
			newH = (int) (mCharSize * 3);
		}
		setMeasuredDimension(w, newH);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mTextPaint.setTextSize(mCharSize);
		//start at 0,0
		float xPad = .5f * mCharSize;
		float y = mCharSize;;
		float x = xPad;
		if(mPortrait) {
			for(char ch = 'A'; ch <= 'J'; ch++) {
				canvas.drawText(String.valueOf(ch), x, y, mTextPaint);
				x += 2 * mCharSize;
			}
			
			x = xPad;
			y += 1.5f * mCharSize;
			for(char ch = 'K'; ch <= 'T'; ch++) {
				canvas.drawText(String.valueOf(ch), x, y, mTextPaint);
				x += 2 * mCharSize;
			}
			
			x = xPad;
			y += 1.5f * mCharSize;
			for(char ch = 'U'; ch <= 'Z'; ch++) {
				canvas.drawText(String.valueOf(ch), x, y, mTextPaint);
				x += 2 * mCharSize;
			}
			//draw delete character
			canvas.drawText(mDelete, x, y, mTextPaint);
			x += 2 * mCharSize;
			canvas.drawText("<", x, y, mTextPaint);
			x += 2 * mCharSize;
			canvas.drawText(">", x, y, mTextPaint);
		}
		else {
			for(char ch = 'A'; ch <= 'O'; ch++) {
				canvas.drawText(String.valueOf(ch), x, y, mTextPaint);
				x += 2 * mCharSize;
			}
			
			x = xPad;
			y += 1.5f * mCharSize;
			for(char ch = 'P'; ch <= 'Z'; ch++) {
				canvas.drawText(String.valueOf(ch), x, y, mTextPaint);
				x += 2 * mCharSize;
			}
			//again, draw delete character
			canvas.drawText(mDelete, x, y, mTextPaint);
			x += 2 * mCharSize;
			canvas.drawText("<", x, y, mTextPaint);
			x += 2 * mCharSize;
			canvas.drawText(">", x, y, mTextPaint);
		}
	}	
	
	/**
	 * Takes the x and y values of a touch event and returns the appropriate character.
	 * If the delete section is touched, return 0.
	 * If no character is touched, return ' '.
	 * @param x
	 * @param y
	 * @return char
	 */
	public char getLetter(float x, float y) {
		
		int row = (int) Math.ceil(y / (mCharSize * 1.5f));
		
		int charNum = (int) Math.ceil(x / (mCharSize * 2));
		
		char ret;
		if(mPortrait) {
			if(row == 1) {
				ret = (char) ('A' + charNum - 1);
			}
			else if(row == 2) {
				ret = (char) ('K' + charNum - 1);
			}
			else {
				if(charNum <= 6)
					ret = (char) ('U' + charNum - 1);
				else if(charNum == 7)
					ret = 0;
				else if(charNum == 8)
					ret = '<';
				else if(charNum == 9)
					ret = '>';
				else
					ret = ' ';
			}
		}
		else {
			if(row == 1) {
				ret = (char) ('A' + charNum - 1);
			}
			else {
				if(charNum <= 11)
					ret = (char) ('P' + charNum - 1);
				// delete = 12th char
				else if(charNum == 12)
					ret = 0;
				else if(charNum == 13)
					ret = '<';
				else if(charNum == 14)
					ret = '>';
				else
					ret = ' ';
			}
		}
		return ret;
	}
	
	/**
	 * utility for other views to know how much space the keyboard will need.
	 * TODO: make chars per row by orientation part of the class
	 * @param w
	 * @param portrait
	 * @return the height of the element
	 */
	public static int getHeight(float w, Boolean portrait) {
		float charSize;
		if(portrait) {
			charSize = w / 20f;
			//multiplying by 1.2 gives a little breathing room at the bottom
			return (int) (charSize * 4 * 1.2);
		}
		else {
			charSize = w / 30f;
			return (int) (charSize * 3 * 1.1);
		}
	}
}