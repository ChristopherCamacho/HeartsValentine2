package Hearts;

import java.awt.Rectangle;

public class TextRectDetails {
	TextRectDetails(Rectangle boundingRect) {
		this.boundingRect = boundingRect;
		endOfLine = false;
	}	
	void setText(String text) {
		this.text = text;
	}	
	void setTextWidth(int textWidth) {
		this.textWidth = textWidth;
	}
	void setEndOfLine() {
		this.endOfLine = true;
	}
	Rectangle getBoundingRect(){
		return boundingRect;
	}
	String getText() {
		return text;
	}	
	int getTextWidth() {
		return textWidth;
	}
	boolean getEndOfLine() {
		return endOfLine;
	}
	
	private final Rectangle boundingRect;
	private String text;
	private int textWidth;
	private boolean endOfLine;
}
