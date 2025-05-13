package Hearts;

public final class UserExceptionDetails {
	UserExceptionDetails(String word, int wordWidth, int handlerRet) {
		this.word = word;
		this.wordWidth = wordWidth;
		this.handlerRet = handlerRet;
	}
	
	String getWord() {
		return word;
	}
	
	int getWordWidth() {
		return wordWidth;	
	}

	int getHandlerRet() {
		return handlerRet;
	}
	
	private String word;
	private int wordWidth;
	private int handlerRet;
}
