package Hearts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextInputDetails {
	
	TextInputDetails() {
		initialized = false;
	}
	
	void initialise(String contentTextFileAndPath) throws FileNotFoundException {

		File file = new File(contentTextFileAndPath);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			lineDetailsLst.add(new TextLineDetails(scanner.nextLine()));
		}
		
		initialized = true;
	}
	
	boolean isInitialized() {
		return initialized;
	}
	
	void resetAllTextFits() {
		for(TextLineDetails lineDetails : lineDetailsLst) {  
			lineDetails.setAllTextFits(true);
		}
	}
	
	boolean allTextFits() {
		for (int i = 0; i < lineDetailsLst.size(); i++) {
			if (!lineDetailsLst.get(i).getAllTextFits()) {  
				return false;
			}
		}
		return true;
	}
	
	TextLineDetails getLineDetails(int idx) {
		return lineDetailsLst.get(idx);
	}

	int count() {
		return lineDetailsLst.size();
	}

	private List<TextLineDetails> lineDetailsLst = new ArrayList<TextLineDetails>();
	private boolean initialized;
}
