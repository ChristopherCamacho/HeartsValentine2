package Hyphens;
//import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResourceHyphenatePatternsLoader {
	public ResourceHyphenatePatternsLoader(HyphenatePatternsLanguage hpl) {
		this.hpl = hpl;
	}
		
	public void load() {
		
		try {
			FileInputStream inputStream = new FileInputStream(System.getenv(getEnvString()));	
			//	File file = new File(System.getenv(getEnvString())); Does not work for larger files like hyph-en-gb.tex!
			Scanner scanner = new Scanner(inputStream);
			boolean readPatterns = false;
			boolean readHyphenation = false;
			boolean reset = false;

			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				
				if (data.length() > 0 && data.charAt(0) != '%') {			
					if (data.contains("%") == true) {
						data = data.substring(0, data.indexOf('%'));
					}
					data = data.trim();
							
					if (data.equals("\\patterns{")) {
						readPatterns = true;
						continue;
					}
					else if (data.equals("\\hyphenation{")) {
						readHyphenation = true;	
						continue;
					}
					else if (data.contains("}") == true) {
						data = data.substring(0, data.indexOf('}'));
						data = data.trim();
						reset = true;
					}
					// The file hyph-fr.tex has lots of lines with an apostrophe outside of a comment.
					// This seems to mean line should be ignored.
					else if (data.contains("'") == true) {
						continue;
					}
					
					if (data.contains(" ") == true) {
						String[] dataSplit = data.split(" ");
						
						for (String str : dataSplit) {
							addElement(str, readPatterns, readHyphenation);
						}
					}
					else {
						addElement(data, readPatterns, readHyphenation);
					}

					if (reset) {
						readPatterns = false;
						readHyphenation = false;
						reset = false;
					}	
				}
			}
			scanner.close();
	    } catch (FileNotFoundException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	}
	
	public List<String> getPatterns() {
		return patterns;
	}
	
	public List<String> getExceptions() {
		return exceptions;
	}
	
	private void addElement(String tag, boolean readPatterns, boolean readHyphenation) {

		if (readPatterns) {
			patterns.add(tag);
		}
		else if (readHyphenation) {
			exceptions.add(tag);
		}		
	}
	
	private String getEnvString() {
		switch (hpl) {
		case EnglishBritish:
			return "INPUT_PATH_HYPHEN_EN_GB";
		case EnglishUs:
			return "INPUT_PATH_HYPHEN_EN_US";
		case French:
			return "INPUT_PATH_HYPHEN_FR";
		case German:
			return "INPUT_PATH_HYPHEN_DE";
		}
		return null;
	}

	private HyphenatePatternsLanguage hpl;
	private List<String> patterns = new ArrayList<String>();
	private List<String> exceptions = new ArrayList<String>();
}
