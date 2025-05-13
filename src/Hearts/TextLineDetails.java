package Hearts;

//import java.awt.Graphics2D;

public class TextLineDetails {
	TextLineDetails(String line) {
		this.line = line;
		allTextFits = false;
	}

	String getLine() {
		return line;
	}
	
	boolean getAllTextFits() {
		return allTextFits;
	}
	
	void setAllTextFits(boolean allTextFits) {
		this.allTextFits = allTextFits;
	}
		
	private final String line;
	private boolean allTextFits;
}
