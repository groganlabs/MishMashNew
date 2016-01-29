package com.groganlabs.mishmash;

public class DqChar {
	//answer = the index of the answerArr we're linked to
	private int answer;
	//letter = the letter displayed
	private char letter;
	
	public DqChar(char letter) {
		this.letter = letter;
		answer = -1;
	}
	
	public void setAnswer(int answer) {
		this.answer = answer;
	}
	
	public void clearAnswer() {
		answer = -1;
	}
	
	public int getAnswer() {
		return answer;
	}
	
	public char getLetter() {
		return letter;
	}
	
	public char[] getLetterArr() {
		char[] letterArr = {letter};
		return letterArr;
	}
	
	public boolean isAnswerSet() {
		return (answer >= 0) ? true : false;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null) return false;
		if(!(o instanceof DqChar)) return false; 
		return ((DqChar)o).answer == this.answer && ((DqChar)o).letter == this.letter;
	}
}

