package Hearts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.w3c.dom.css.Rect;

import Hyphens.HyphenatePatternsLanguage;
import Hyphens.Hyphenator;

final class DrawText {
	
	DrawText(Graphics2D g2d, MainSizes mainSizes, HeartDetails hd, TextFormattingDetails tfd) {
		this.g2d = g2d;
		this.mainSizes = mainSizes;
		this.hd = hd;
		this.tfd = tfd;
		
		if (HyphenatorLangMap.containsKey(tfd.gethyphenPatternLan())) {
			this.hyphenator = HyphenatorLangMap.get(tfd.gethyphenPatternLan());
		}
		else {
			this.hyphenator = new Hyphenator(tfd.gethyphenPatternLan());
			HyphenatorLangMap.put(tfd.gethyphenPatternLan(), this.hyphenator);
		}
		Font newFont = new Font("TimesRoman", Font.PLAIN, 150);			
	//	Font newFont = new Font("Linux Libertine G Bold", Font.PLAIN, 150);
		g2d.setFont(newFont);
		
		this.hyphenWidth = getCharWidth(hyphen);
		textAscent = g2d.getFontMetrics().getAscent();// - g2d.getFontMetrics().getDescent();//.getAscent();//.getHeight();
		textDescent = g2d.getFontMetrics().getDescent();
	}

	private int[] getXCircleIntersectionsFromY(int y, boolean left) {
		// centre of left circle is x_centre = margin + 2*heartCenterX + (radius - heartCenterX) = margin + heartCenterX + radius
		// 					        y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
		// centre of right circle is x_centre = width - margin - 2 * radius + radius - heartCenterX = width - margin - radius - heartCenterX
		// 					   y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius			
		// equation of top left inner circle is:
		// (margin + heartCenterX + radius) + (radius - heartCenterX)*cos(angle)
		// (margin - heartCenterY + radius) + (radius + heartCenterY)*sin(angle) where angle = 0 to 2*pi. 
		// equation of top right inner circle is:
		// (width - margin - radius - heartCenterX) + (radius - heartCenterX)*cos(angle)
		//  (margin - heartCenterY + radius) + (radius + heartCenterY)*sin(angle) where angle = 0 to 2*pi. 
		// these are not circles but ovals! 		 
		double angle = Math.asin((y - (mainSizes.getMargin() - hd.getHeartCenterY() + mainSizes.getRadius()))/(mainSizes.getRadius() - tfd.getTxtHeartsMargin() + hd.getHeartCenterY()));
		double xCentre = left? (mainSizes.getMargin() + hd.getHeartCenterX() + mainSizes.getRadius()) : (mainSizes.getWidth() - mainSizes.getMargin() - mainSizes.getRadius() - hd.getHeartCenterX());	
		int xFirst = (int)(xCentre + (mainSizes.getRadius() - tfd.getTxtHeartsMargin() - hd.getHeartCenterX())*Math.cos(angle));
		int xSecond = (int)(xCentre + (mainSizes.getRadius() - tfd.getTxtHeartsMargin() - hd.getHeartCenterX())*Math.cos(angle + Math.PI));
		int[] retVal = new int[2];
		retVal[0] = Math.min(xFirst, xSecond);
		retVal[1] = Math.max(xFirst, xSecond);
		return retVal;
	}

	private int[][] computeBottomTrianglePts() {
		Point2D ptLeftTopCircleCentre = new Point2D.Double(mainSizes.getMargin() + mainSizes.getRadius() + hd.getHeartCenterX(), mainSizes.getMargin() + mainSizes.getRadius() - hd.getHeartCenterY());
		Point2D ptRightTopCircleCentre = new Point2D.Double(mainSizes.getWidth() - mainSizes.getMargin() - mainSizes.getRadius() - hd.getHeartCenterX(), mainSizes.getMargin() + mainSizes.getRadius() - hd.getHeartCenterY());

		double startAngle = Math.acos((mainSizes.getWidth()/2.0 - ptLeftTopCircleCentre.getX())/mainSizes.getRadius());
		double vertDistBottomPt = mainSizes.getHeight() - 2 * mainSizes.getMargin() - mainSizes.getRadius(); // y-coord top circle centres - y coord of bottom of heart
		double alpha = Math.atan(vertDistBottomPt/mainSizes.getRadius());
		double phi = Math.atan(vertDistBottomPt/(mainSizes.getRadius()*Math.cos(startAngle)));
	
		double beta = 2*Math.PI - alpha - phi;
	 	
		double pt1x = ptLeftTopCircleCentre.getX() + mainSizes.getRadius() * Math.cos(beta)  - tfd.getTxtHeartsMargin() / Math.sin(beta)/*- heartCenterX + heartCenterWidth*/;
		double pt1y = ptLeftTopCircleCentre.getY() - mainSizes.getRadius() * Math.sin(beta) - hd.getHeartCenterY() /* + heightAdjustment*/;
	
		double pt2x = ptRightTopCircleCentre.getX() + mainSizes.getRadius() * Math.cos(Math.PI - beta) + tfd.getTxtHeartsMargin() / Math.sin(beta)/*- heartCenterX + heartCenterWidth*/;
		double pt2y = ptRightTopCircleCentre.getY() - mainSizes.getRadius() * Math.sin(Math.PI - beta) - hd.getHeartCenterY() /*+ heightAdjustment*/;
		
		double pt3x = mainSizes.getWidth()/2;
		double pt3y = mainSizes.getHeight() - mainSizes.getMargin() + tfd.getTxtHeartsMargin() / Math.cos(Math.PI/2.0 - beta);
		
		double zeta = Math.atan ( (pt1y - pt3y)/(pt1x - pt3x));
		double heightAdjustment = -0.5 * hd.getHeartWidth() * Math.sin(zeta) - hd.getHeartHeight();

		pt1y += heightAdjustment;
		pt2y += heightAdjustment;
		pt3y += heightAdjustment;
	
		int[] xpoints = {(int)pt1x, (int)pt3x, (int)pt2x};
		int[] ypoints = {(int)pt1y, (int)pt3y, (int)pt2y};
	 
		int[][] retVal = {xpoints, ypoints};
		return retVal;
	}

	// drawTopCircles draws the top two bounding disks inside hearts
	void drawShapes(int[][] pts) {
		// top left oval
		g2d.setColor(Color.blue);		
		g2d.fillArc((int) (mainSizes.getMargin() + tfd.getTxtHeartsMargin() + 2*hd.getHeartCenterX()), (int) (mainSizes.getMargin() - 2*hd.getHeartCenterY() + tfd.getTxtHeartsMargin()),
				    (int)(2*(mainSizes.getRadius() - hd.getHeartCenterX() - tfd.getTxtHeartsMargin())), (int)(2*(mainSizes.getRadius() - tfd.getTxtHeartsMargin() + hd.getHeartCenterY())),
				    							   0, 360);
		// Top right oval
		g2d.setColor(Color.cyan);	
		g2d.fillArc((int) (mainSizes.getWidth() - mainSizes.getMargin() - 2*mainSizes.getRadius()  + tfd.getTxtHeartsMargin()), (int) (mainSizes.getMargin() - 2*hd.getHeartCenterY()  + tfd.getTxtHeartsMargin()),
					 (int)(2*(mainSizes.getRadius() - tfd.getTxtHeartsMargin() - hd.getHeartCenterX())), (int)(2*(mainSizes.getRadius() - tfd.getTxtHeartsMargin() + hd.getHeartCenterY())),
                                              	    0, 360);
		
		Polygon p = new Polygon(pts[0], pts[1], 3);
		 
		g2d.setColor(Color.green);
		g2d.drawPolygon(p); 
		g2d.fillPolygon(p);
	}
 
	void drawTextBoundingRectangles() {	
		int counter = 0;
		 
		for (TextRectDetails txtRectDets : rectLst) {
			if (counter%4 == 0) {
				g2d.setColor(Color.magenta);
			} else if (counter%4 == 1) {
				g2d.setColor(Color.orange);
			} else if (counter%4 == 2) {
				g2d.setColor(Color.pink);
			} else {
				g2d.setColor(Color.yellow);
			}
			g2d.fillRect(txtRectDets.getBoundingRect().x, txtRectDets.getBoundingRect().y,
					txtRectDets.getBoundingRect().width, txtRectDets.getBoundingRect().height);
			counter++;
		}	 
	}
	 
	private void computeTextRectangles() {
		int[][] pts = computeBottomTrianglePts();
		 	
		// Uncomment line below for testing purposes
        //drawShapes(pts);
			
		int yTop = (int)(tfd.getTopTextMargin() + mainSizes.getMargin() - 2*hd.getHeartCenterY());	
			
		while (yTop < pts[1][0]) {
			int[] xTopLeft =  getXCircleIntersectionsFromY(yTop, true);
			int[] xTopRight =  getXCircleIntersectionsFromY(yTop, false);
			
			int yBottom = yTop + textAscent;
			int[] xBottomLeft =  getXCircleIntersectionsFromY(yBottom, true);
			int[] xBottomRight =  getXCircleIntersectionsFromY(yBottom, false);

			int xLeft = Math.max(xTopLeft[0], xBottomLeft[0]);
			int xLeftWidth = Math.min(xTopLeft[1] - xTopLeft[0], xBottomLeft[1]-xBottomLeft[0]);
			int xRight = Math.max(xTopRight[0], xBottomRight[0]);
			int xRightWidth = Math.min(xTopRight[1] - xTopRight[0], xBottomRight[1]-xBottomRight[0]);
			
			// The 2 rectangles can be very close, without intersecting and without these intersecting heart if these were joined. When this happens, funny result with hyphen added in middle of line.
            // see INPUT_PATH_SILLY_MESSAGE with text to heart margin of 20
            // To remedy this, let us also merge if gap is very small - say half of heart width.
            // Hope not introducing bug here.
            int maxGap = (int) (hd.getHeartWidth() / 2.0);
            
			if (xLeft + xLeftWidth + maxGap < xRight && yTop < (mainSizes.getMargin() - hd.getHeartCenterY() + mainSizes.getRadius()) && yBottom < (mainSizes.getMargin() - hd.getHeartCenterY() + mainSizes.getRadius())) {
				rectLst.add(new TextRectDetails(new Rectangle(xLeft, yTop, xLeftWidth, textAscent)));	
				rectLst.add(new TextRectDetails(new Rectangle(xRight, yTop, xRightWidth, textAscent)));
			}
			else {
				rectLst.add(new TextRectDetails(new Rectangle(xLeft, yTop, xRight - xLeft + xRightWidth, textAscent)));
			}
			yTop += tfd.getLineHeight();
		}
			
		while (yTop < pts[1][1]) {
			// equation of a line through (a, b) = pt[0][0], pt[1][0] or pt[0][1], pt[1][1]   
			// and (c, d) = pt[0][1], pt[1][1] is:
			// x + aa*y + bb = 0 where:
			// aa = (c - a)/(b - d) and bb = (ad - bc)/(b - d)
			double aa1 = (pts[0][1] - pts[0][0])/(double)(pts[1][0] - pts[1][1]);			
			double bb1 = (pts[0][0]*pts[1][1] - pts[1][0]*pts[0][1])/(double)(pts[1][0] - pts[1][1]);
				
			double aa2 = (pts[0][1] - pts[0][2])/(double)(pts[1][2] - pts[1][1]);
			double bb2 = (pts[0][2]*pts[1][1] - pts[1][2]*pts[0][1])/(double)(pts[1][2] - pts[1][1]);
			// so x = - bb - aa*y
				
			int y = yTop + textAscent;
			int x1 = (int) (- bb1 - aa1*y);
			int x2 = (int) (- bb2 - aa2*y);

			rectLst.add(new TextRectDetails(new Rectangle(x1, yTop, x2 - x1, textAscent)));
			yTop += tfd.getLineHeight();		
		}
		// Uncomment for testing purpose
		//drawTextBoundingRectangles();
	}
	
	int computeTextSpaceAvailable() {
		rectLst = new ArrayList<TextRectDetails>();
		computeTextRectangles();	
		int retVal = 0;
		
		for (TextRectDetails txtRectDets : rectLst) {
			retVal += txtRectDets.getBoundingRect().width;
		}
		
		return retVal;
	 } 
	
	private void HandleWordInProgressNoPunc(StringBuilder wordInProgressNoPunc, StringBuilder punctuations, List<String> brokenwordsList) {
		if (wordInProgressNoPunc.length() > 0) {
			String[] brokenWords = hyphenator.hyphernateWord(wordInProgressNoPunc.toString());
		
			if (brokenwordsList.size() == 0) {
				brokenWords[0] = punctuations.toString() + brokenWords[0];
				Collections.addAll(brokenwordsList, brokenWords);
			}
			else {
				brokenwordsList.set(brokenwordsList.size() - 1, brokenwordsList.get(brokenwordsList.size() - 1) + punctuations.toString() + brokenWords[0]);
				
				for (int i = 1; i < brokenWords.length; i++) {
					brokenwordsList.add(brokenWords[i]);
				}			
			}
			punctuations.setLength(0);
			wordInProgressNoPunc.setLength(0);
		}
	}
	
	private int getCharWidth(char chr) {
			
		if (charWidthMap.containsKey(chr)) {
			return charWidthMap.get(chr);
		}
		
		int width = g2d.getFontMetrics().charWidth(chr);	
		charWidthMap.put(chr, width);

		return width;
	}
	
 
	 // Computes bounding rectangles and text inside each of ther
	void computeTextPlacementDetails() {
		rectLst = new ArrayList<TextRectDetails>();
		char chr;
		StringBuilder lineInProgress = new StringBuilder();
		StringBuilder wordInProgress = new StringBuilder();
		int lineWidth = 0; // The total width of characters on line inside a bounding rectangle.
		int wordWidth = 0; // The total width of characters of a word.
		boolean noHyphenWord = false; // This is set to true when we are not hyphenating a particular word.

		try {
			computeTextRectangles();
			Iterator<TextRectDetails> rectLstIterator = rectLst.iterator();
				
			if (rectLstIterator.hasNext()) {
				TextRectDetails txtRectDets = rectLstIterator.next();
				Rectangle boundingRect = txtRectDets.getBoundingRect();
				
				if (!textInputDetails.isInitialized()) {
					textInputDetails.initialise(tfd.getContentTextFileAndPath());
				}
				else {
					textInputDetails.resetAllTextFits();
				}
				
				for (int lineDetIdx = 0; lineDetIdx < textInputDetails.count(); lineDetIdx++) {
				
		//		File file = new File(tfd.getContentTextFileAndPath());
		//		Scanner scanner = new Scanner(file);
		//		while (scanner.hasNextLine()) {
		//			String data = scanner.nextLine();
					TextLineDetails txtLineDets = textInputDetails.getLineDetails(lineDetIdx);
					String data = txtLineDets.getLine();
					
					if (data.length() != 0) {
					// We need the last word to know if optimized size for placing all text.
					// If use just last text in rectangle, this text may already have been hyphenated so no good...		
						String dataTrim = data.trim();
						// Safe if no ' ' in string. lastIndexOf returns -1 so lastWord = substring(0) - same as dataTrim - desired effect
						lastWord = dataTrim.substring(dataTrim.lastIndexOf(" ") + 1);		
						lineWidth = 0;
						txtLineDets.setAllTextFits(false);
					}
				
					for (int i = 0; i < data.length(); i++) {
						chr = data.charAt(i);				
						int charWidth = getCharWidth(chr);
						// Handling of the { } and {-} here...
						if (chr == '{' && i < data.length() - 2) {
							int handlerRet = -1;
							
							if (data.charAt(i + 1) == ' ') {
								// Handling of "{ }" below. (non-break space)
								if (data.charAt(i + 2) == '}') {
									wordInProgress.append(nonBreakSpace);
									wordWidth += getCharWidth(nonBreakSpace);
									handlerRet = i + 2;
								}
								// Handling of "{  }" below - so user can enter "{ }".
								else if (data.charAt(i + 2) == ' ') {
									// in order to allow user to enter "{ }", all user has to do is enter "{  }" (2 spaces and one is removed) 
									wordInProgress.append(chr);
									wordWidth += charWidth;
									handlerRet = i + 1;
								}
								// else invalid entry warning?
							}
							if (data.charAt(i + 1) == 'x') {
								// Handling of "{x}" below. (no hyphenation for word following)
								if (data.charAt(i + 2) == '}') {
									noHyphenWord = true;
									handlerRet = i + 2;
								}
								// Handling of "{x}" below - so user can enter "{x}".
								else if (data.charAt(i + 2) == 'x') {
									wordInProgress.append(chr);
									wordWidth += charWidth;
									handlerRet = i + 1;
								}
							}
							else if (data.charAt(i + 1) == '-') {
								if (data.charAt(i + 2) == '}') {
									// We only need to perform computations below once so store result in a map
									if (usrExceptionMap.containsKey(i)) {
										UserExceptionDetails usrExceptionDets = usrExceptionMap.get(i);
										wordInProgress.setLength(0);		
										wordInProgress.append(usrExceptionDets.getWord());
										wordWidth = usrExceptionDets.getWordWidth();
										handlerRet = usrExceptionDets.getHandlerRet();						
									}
									else {				
										// Normally, the start of new exception we want for hyphenation is wordInProgress
										// Strictly speaking, that is not always correct. User might have added a non space break prefixing word.
										// User might also have entered inverted exclamation mark or inverted question mark (used in Spanish)
										// We will handle those possibilities...
										int nbsPos = -1;
									
										for (int idx = 0;  idx < wordInProgress.length(); idx++) {
											if (!Character.isJavaIdentifierPart(wordInProgress.charAt(idx))) {
												nbsPos = idx;
											}
										}
										
										ArrayList<String> exception = new ArrayList<String>();
										 
										if (nbsPos == -1) {
											exception.add(wordInProgress.toString());
										} 
										else {
											exception.add(wordInProgress.substring(nbsPos + 1)); // test case last chr nbs
										}
										int j = i + 3;
										StringBuilder wordFragment = new StringBuilder();
									
										while (j < data.length()) {
											char currChr = data.charAt(j);
											
											if (!Character.isJavaIdentifierPart(currChr) && currChr != '{')
												break;
											
											if (currChr == '{' && j < data.length() - 2 && data.charAt(j+1) == '-' && data.charAt(j+2) == '}') {
												exception.add(wordFragment.toString());
												wordFragment.setLength(0);
												j += 2;
											}
											else {
												wordInProgress.append(currChr);
												wordWidth += getCharWidth(currChr);
												wordFragment.append(currChr);
											}	
											++j;
										}
									
										exception.add(wordFragment.toString());
										handlerRet = j - 1;
										hyphenator.addException(exception.toArray(new String[0]));
										usrExceptionMap.put(i, new UserExceptionDetails(wordInProgress.toString(), wordWidth, handlerRet));					
									}
								} 
								else if (data.charAt(i + 2) == '-') {
									// in order to allow user to enter "{-}", all user has to do is enter "{--}" (2 hyphens and one is removed) 
									wordInProgress.append(chr);
									wordWidth += charWidth;
									handlerRet = i + 1;
								}			
							}
							if (handlerRet != -1) {
								i = handlerRet; // move so many characters ahead
								continue;
							}
						}
						
						if (chr != ' ') {
							wordInProgress.append(chr);
							wordWidth += charWidth;
						}
						if (chr == ' ' || i == data.length() - 1) {
							// In French, non-breaking space before exclamation, question marks or colon
							if (i != data.length() - 1 && (data.charAt(i + 1) == '!' || data.charAt(i + 1) == '?' || data.charAt(i + 1) == ':')) {
								wordInProgress.append(nonBreakSpace);
								wordWidth += charWidth;
								continue;
							}
							
							if (lineWidth + wordWidth <= boundingRect.width) {
								lineInProgress.append(wordInProgress);
								lineWidth += wordWidth;
								wordInProgress.setLength(0);
								wordWidth = 0;
								
								if (chr == ' ' && lineWidth + charWidth <= boundingRect.width) {						
									lineInProgress.append(chr);
									lineWidth += charWidth;
								}
								if (i == data.length() - 1) {
									txtLineDets.setAllTextFits(true);
								}
							}		
							else {	
								if (tfd.getHyphenateText() && noHyphenWord == false) {
									// wordInProgress may have punctuation marks.
									// No hyphenation can take place around these punctuation marks.
									// I have made that assumption in calculations below.
									// Finish punctuation marks may be multiple such as ?! or ...
									// I have handled inverted Spanish exclamation and question marks prefixing a word.
									// You can have apostrophe in middle of wordInProgress - example: "isn't"
									StringBuilder wordInProgressNoPunc = new StringBuilder();
									StringBuilder punctuations = new StringBuilder();
								    List<String> brokenwordsList = new ArrayList<>();

									for (int idx = 0;  idx < wordInProgress.length(); idx++) {
										if (Character.isJavaIdentifierPart(wordInProgress.charAt(idx))) {
											wordInProgressNoPunc.append(wordInProgress.charAt(idx));
										}
										else {
											HandleWordInProgressNoPunc(wordInProgressNoPunc, punctuations, brokenwordsList);	
											punctuations.append(wordInProgress.charAt(idx));	
										}
									}
									// Case space immediately after wordInProgressNoPunc
									HandleWordInProgressNoPunc(wordInProgressNoPunc, punctuations, brokenwordsList);
									
									// Case punctuation marks after wordInProgressNoPunc: simply append these 
									if (punctuations.length() > 0) {
										if (brokenwordsList.size() > 0) {
											brokenwordsList.set(brokenwordsList.size() - 1, brokenwordsList.get(brokenwordsList.size() - 1) + punctuations.toString());
										}
										else {
											brokenwordsList.add(punctuations.toString());
										}
									}
									
									boolean breakLoop = false;
				
									for (int idx = 0; idx < brokenwordsList.size(); idx++) {
										String str = brokenwordsList.get(idx);
										int brokenWordWidth = g2d.getFontMetrics().charsWidth(str.toCharArray(), 0, str.length());
										int requiredWidth = (idx < brokenwordsList.size() - 1)? brokenWordWidth + hyphenWidth: brokenWordWidth;
										
										if (lineWidth + requiredWidth <= boundingRect.width) {
											lineInProgress.append(str);
											lineWidth += brokenWordWidth;
										}		
										else {
											if (idx > 0) {
												lineInProgress.append(hyphen);
												lineWidth += hyphenWidth;		
											}
											txtRectDets.setText(lineInProgress.toString());	
											txtRectDets.setTextWidth(lineWidth);
											
											// BEWARE: not enough space in previous rectangle does not necessarily mean there will not be enough in any of next rectangles.
											boolean badFit = true;
											
											while (rectLstIterator.hasNext() && badFit) {
												txtRectDets = rectLstIterator.next();
												boundingRect = txtRectDets.getBoundingRect();
												
												// make sure not last. Otherwise mustn't affix hyphen width
												if (requiredWidth <= boundingRect.width) {
													badFit = false;
												}
											}
											
											if (!rectLstIterator.hasNext()) {
												breakLoop = true;
												break;	
											}
											lineInProgress.setLength(0);
											lineInProgress.append(str);
											lineWidth = brokenWordWidth;
										}	
									}					
									// We have processed word in progress, so reset this...
									wordInProgress.setLength(0);
									wordWidth = 0;
									
									if (breakLoop) {
										break;
									} 
									else if ( i == data.length() - 1) {
										txtLineDets.setAllTextFits(true);
									}			
								}	
								else {
									txtRectDets.setText(lineInProgress.toString());	
									txtRectDets.setTextWidth(lineWidth);
									lineInProgress.setLength(0);
									lineWidth = 0;
									
									// BEWARE: not enough space in this rectangle does not necessarily mean there will not be enough in next rectangle.
									// Rectangles start by getting wider
									boolean badFit = true;
								//	int currentWidth = txtRectDets.getBoundingRect().width;
									
									while (rectLstIterator.hasNext() && badFit) {
										txtRectDets = rectLstIterator.next();
										boundingRect = txtRectDets.getBoundingRect();
										
										if (wordWidth <= boundingRect.width) {
											badFit = false;
										}
										
									//	currentWidth = txtRectDets.getBoundingRect().width;
									}
									
									if (!badFit && i == data.length() - 1) {
										txtLineDets.setAllTextFits(true);
									}
							
									if (!rectLstIterator.hasNext()) {
										break;	
									}
								}
								if (chr == ' ' && i != data.length() - 1) {
									i--; // We are changing bounding rectangle, so we have to go back so we re-process the space we have just
								         // done on next bounding box and we add word that didn't fit into next bounding rectangle that way..	
								}
							}
							noHyphenWord = false;
						}
					}
					// Don't forget to add last line.
					if (lineWidth + wordWidth <= boundingRect.width) {
						lineInProgress.append(wordInProgress);
						lineWidth += wordWidth;
						txtRectDets.setText(lineInProgress.toString());	
						txtRectDets.setTextWidth(lineWidth);
						lineInProgress.setLength(0);
						wordInProgress.setLength(0);
						lineWidth = 0;
						// here end of line set to true
						txtRectDets.setEndOfLine();
						txtLineDets.setAllTextFits(true);
					}
					else {
						// We couln't place last piece of text...
						txtLineDets.setAllTextFits(false);
					}
					
					// have to make sure next rectangle is lower - top 2 rectangles have same y coordinate.
					int boundingRectY = boundingRect.y;
					boolean yChanged = false;
					while (rectLstIterator.hasNext() && !yChanged) {	
						txtRectDets = rectLstIterator.next();
						boundingRect = txtRectDets.getBoundingRect();
						if (boundingRect.y != boundingRectY) {
							yChanged = true;
						}
					}				
				}
		//		scanner.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred reading " + tfd.getContentTextFileAndPath() + " file.");
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			System.out.println("An error occurred with call to rectLstIterator.next() file.");
			e.printStackTrace();		
		} catch (ConcurrentModificationException e) {
			System.out.println("An error occurred with call to rectLstIterator.next() file.");
			e.printStackTrace();			
		} catch (Exception e) {
			System.out.println("An error occurred in method computeTextPlacementDetails().");
			e.printStackTrace();
		}
	}
		 		
	void draw() {
		try {
			g2d.setColor(Color.black);	
	
			Iterator<TextRectDetails> rectLstIterator = rectLst.iterator();
		
			while (rectLstIterator.hasNext()) {
				TextRectDetails txtRectDets = rectLstIterator.next();
			
				Rectangle txtBoundingRect = txtRectDets.getBoundingRect();
				String txt = txtRectDets.getText();
			
				if (txt != null && txt.length() > 0) {
					 if (tfd.getOptimizeSpacing()) {
						 int availableWidth = txtRectDets.getBoundingRect().width;
						 txt = txt.trim();
						 int charsWidths[] = new int[txt.length()];
						 int usedWidth = 0;
						 int interCharRatio = 0; // sum of all chars x2 except 1st and last x1
						// getLineMetrics(str, beginIndex, limit, context).charWidth(txt.charAt(idx));
						 
						 
						 for (int idx = 0; idx < txt.length(); idx++) {
							 charsWidths[idx] = getCharWidth(txt.charAt(idx));
							 usedWidth += charsWidths[idx];
							 
							 if (idx == 0 || idx == txt.length() - 1) {
								 interCharRatio += charsWidths[idx];
							 }
							 else {
								 interCharRatio += 2 * charsWidths[idx];
							 }
						 }
						 
						 int excessWidth = availableWidth - usedWidth;
						 double excessUsedWidthRatio = excessWidth/usedWidth; 
						 double offset = 0;
						 
						 if (excessUsedWidthRatio > maxExcessWidth) {
							 excessWidth = (int)(usedWidth * (double)maxExcessWidth);	 
						 } 
						 
						 char[] chr = new char[1];
						 
						 for (int idx = 0; idx < txt.length(); idx++) {
							 
							 if (idx > 0) {
								 offset += (charsWidths[idx] * excessWidth)/(double)interCharRatio;
							 }
							 
							 chr[0] = txt.charAt(idx);
							 g2d.drawChars(chr, 0, 1, (int) (txtBoundingRect.x + Math.round(offset)), txtBoundingRect.y + txtBoundingRect.height - textDescent);		 

							 if (idx < txt.length() - 1) {
								 offset += charsWidths[idx];
								 offset += (charsWidths[idx] * excessWidth)/(double)interCharRatio;
							 }
						 }
					 }
					 else {
						 g2d.drawChars(txt.toCharArray(), 0, txt.length(), txtBoundingRect.x, txtBoundingRect.y + txtBoundingRect.height - textDescent);
					 }
				}
			}	
		} catch (Exception e) {
			System.out.println("An error occurred in method draw().");
			e.printStackTrace();
		}
	}
	// Returns true if:
	// - All rectangles optimised.
	// - Last rectangles are empty but last word (no hyphenation) or last hyphenated word fragment are wider than these rectangle.
	// Otherwise returns false.	
	boolean SizeOptimized() {
		boolean optimized = false;
		ListIterator<TextRectDetails> li = rectLst.listIterator(rectLst.size());
		int lastRectWidth = 0;
		int previousRectWidth = 0;
		int lastWordSegmentWidth = 0;

		if (tfd.getHyphenateText()) {
			String[] brokenWords = hyphenator.hyphernateWord(lastWord);
			String lastWordSegment = "-" + brokenWords[brokenWords.length - 1];
			lastWordSegmentWidth = g2d.getFontMetrics().charsWidth(lastWordSegment.toCharArray(), 0, lastWordSegment.length());;
		}
		
		if (li.hasPrevious()) {
			TextRectDetails trd = li.previous();
			
			if (trd.getTextWidth() > 0) {
				// All rectangles used...
				optimized = true;
			}
			else { 	
				List<Integer> lastRectsWidths = new ArrayList<>();
				
				while(li.hasPrevious()) {
					previousRectWidth = lastRectWidth;
					lastRectWidth = trd.getBoundingRect().width;
					
					if (previousRectWidth > lastRectWidth) {
						// Heart is getting narrower here - useless processing any further.
						break;
					}

					lastRectsWidths.add(lastRectWidth);
					trd = li.previous();
					
					if (trd.getTextWidth() > 0) {
						
						if (tfd.getHyphenateText()) {
							if (lastWordSegmentWidth < previousRectWidth) {
								optimized = false;
								break;
							}			
						}
						else {
							// If the last word in rectangle is wider than rectangle below, then it is optimized.	
							String lastWordInRect = trd.getText().substring(trd.getText().lastIndexOf(" ") + 1);
							int lastWordInRectWidth = g2d.getFontMetrics().charsWidth(lastWordInRect.toCharArray(), 0, lastWordInRect.length());
						
							if (lastWordInRectWidth > lastRectWidth) {
								optimized = true;
								break;
							}
						}		
					}
				}
			}				
		}			
		return optimized;
	}
	
	boolean DoesAllTextFit() {
		return textInputDetails.allTextFits();
	}
	
	private final Graphics2D g2d;
	private MainSizes mainSizes;
	private final HeartDetails hd;
	private final TextFormattingDetails tfd;
	private int textAscent;
	private int textDescent;
	private Hyphenator hyphenator;
	private final char hyphen = '-';
	private final int hyphenWidth;
	private List<TextRectDetails> rectLst;
	private String lastWord;	
	private static HashMap<HyphenatePatternsLanguage, Hyphenator> HyphenatorLangMap = new HashMap<HyphenatePatternsLanguage, Hyphenator>();
	// Below must be reset and reloaded if text content changes, as must exceptions in HyphenatorLangMap above.
	private static HashMap<Integer, UserExceptionDetails> usrExceptionMap = new HashMap<Integer, UserExceptionDetails>();
	private static HashMap<Character, Integer> charWidthMap = new HashMap<Character, Integer>();
	private static TextInputDetails textInputDetails = new TextInputDetails();
	private static char nonBreakSpace = 0xA0;
	private static double maxExcessWidth = 0.5;
}
