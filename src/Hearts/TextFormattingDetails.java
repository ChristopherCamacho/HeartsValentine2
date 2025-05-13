package Hearts;

import Hyphens.HyphenatePatternsLanguage;

public class TextFormattingDetails {
	
	TextFormattingDetails(String contentTextFileAndPath, boolean optimizeSpacing, boolean hyphenateText, 
			HyphenatePatternsLanguage hyphenPatternLan, int topTextMargin, int lineHeight, int txtHeartsMargin) {
		this.contentTextFileAndPath = contentTextFileAndPath;
		this.optimizeSpacing = optimizeSpacing;
		this.hyphenateText = hyphenateText;
		this.hyphenPatternLan = hyphenPatternLan;
		this.topTextMargin = topTextMargin;
		this.lineHeight = lineHeight;
		this.txtHeartsMargin_ = txtHeartsMargin;
	}
	
	String getContentTextFileAndPath() {
		return contentTextFileAndPath;
	}
	
	boolean getOptimizeSpacing() {
		return optimizeSpacing;
	}
	
	boolean getHyphenateText() {
		return hyphenateText;
	}

	HyphenatePatternsLanguage gethyphenPatternLan() {
		return hyphenPatternLan;
	}
	
	int getTopTextMargin() {
		return topTextMargin;
	}
	
	int getLineHeight() {
		return lineHeight;
	}
	
	int getTxtHeartsMargin() {
		return txtHeartsMargin_;
	}

	private final String contentTextFileAndPath;
	private final boolean optimizeSpacing;
	private final boolean hyphenateText;
	private final HyphenatePatternsLanguage hyphenPatternLan;
	private final int topTextMargin;
	private final int lineHeight;
	private final int txtHeartsMargin_; // The margin between heart frame and actual text. Can be 0.
}
