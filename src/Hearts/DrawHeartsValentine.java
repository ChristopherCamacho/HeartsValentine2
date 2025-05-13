package Hearts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class DrawHeartsValentine {
	DrawHeartsValentine(TextFormattingDetails tfd) {	
		this.tfd = tfd;
		mainSizes = new MainSizes();
		initialize();
	}
	
	private void initialize() {
		initializeGraphics();
		setStandardTextFont();
		computePixelTextLength();
	}
	
	// Initialises an unused graphic object so can calculate text size.
	// We don't know the size of the image till we have the text size.
	// We can only have this by creating an unused graphic object.
	private void initializeGraphics() {
		bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);	    
	    g2d = bufferedImage.createGraphics();
	}
	
	private void setStandardTextFont() {
		Font newFont = new Font("TimesRoman", Font.PLAIN, 150);
		g2d.setFont(newFont);
	}

	
	// Computes the pixel text length. (Not number of characters)
	private void computePixelTextLength() {
		try {
			pixelTxtLen = 0;
			File file = new File(tfd.getContentTextFileAndPath());
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				pixelTxtLen += g2d.getFontMetrics().charsWidth(data.toCharArray(), 0, data.length());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}		
	}
	
	// part of experiment - used for computing gross estimate
	private int textLenFromWidth(int width) {
		hd = new HeartDetails(g2d);
		mainSizes.resetWidthHeigthRadius(width);
		dt = new DrawText(g2d, mainSizes, hd, tfd); 	
		return dt.computeTextSpaceAvailable();
	}
  	
	private int computeWidthEstimateFromTextLen(int lowerBound, int upperBound, int step, int txtLengthEstimate) {
		int retVal = -1;
	 		
  		for (int i = lowerBound; i <= upperBound; i+= step) {	
  			int txtLen = textLenFromWidth(i);
  			
  			if (txtLen >= txtLengthEstimate) {
  				if (step == 1) {
  					retVal = i;
  				}
  				else {
  					retVal = computeWidthEstimateFromTextLen(i - step, i, step/4, txtLengthEstimate);
  				}
  				break;
  			}
  		}
  		return retVal;
	}
  	
  	// Retrieves a gross estimate of with from pixel text length
  	private int getWidthEstimateFromTextLen(int txtLengthEstimate) {
  		int step = stepIncrement;
  		
  		while (step < txtLengthEstimate) {
  			step *= stepIncrement;
  		}
  		int lowerBound = 0;
  		int upperBound = step;
  		step /= stepIncrement;
  
  		return computeWidthEstimateFromTextLen(lowerBound, upperBound, step, txtLengthEstimate);
  	}
	
	void computeTextFit() {
		mainSizes.resetWidthHeigthRadius(getWidthEstimateFromTextLen(pixelTxtLen));
		g2d.dispose();

		boolean goodSize = false;
		int counter = 0;
		boolean decreased = false;
		boolean increased = false;
		boolean smallDecrease = false;
		boolean smallIncrease = false;
		boolean bestOptimization = false;

		do {
			counter++;
			bufferedImage = new BufferedImage(mainSizes.getWidth(), mainSizes.getHeight(), BufferedImage.TYPE_INT_RGB);	    
			g2d = bufferedImage.createGraphics();	    
			hd = new HeartDetails(g2d);
			dt = new DrawText(g2d, mainSizes, hd, tfd); 
			dt.computeTextPlacementDetails();
			
			if (bestOptimization)
				break;
			
			if (dt.DoesAllTextFit()) {
				if (dt.SizeOptimized()) {
					goodSize = true;
				}
				else {
					// Frame is too big. We need to decrease it and try again...
					if (increased) {
						if (smallIncrease) {
							// this is best size we can get. Any smaller, text won't fit...
							goodSize = true;
						}
						else {
							mainSizes.resetWidthHeigthRadius(mainSizes.getWidth() - 1);
							smallDecrease = true;
						}				
					}
					else {
						mainSizes.resetWidthHeigthRadius(mainSizes.getWidth() - 5);
					}	
					decreased = true;
				}				
			}
			else {
				// Frame is too small. We need to increase it and try again...
				if (decreased) {
					mainSizes.resetWidthHeigthRadius(mainSizes.getWidth() + 1);
					smallIncrease = true;
					
					if (smallDecrease) {
						// both small increase and decrease have been call - this is best optimisation we can get.
						// exit loop after this...
						bestOptimization = true;
					}
				}
				else {
					mainSizes.resetWidthHeigthRadius(mainSizes.getWidth() + 5);;	
				}
				increased = true;
			}
			
			if (!goodSize && counter < maxIterations) {
				g2d.dispose();
			}
		}
		while (!goodSize && counter < maxIterations);
	}
	
	void draw() {
		try {
			int closestDistance = 150; // cannot get an exact distance between hearts centres. This is lowest value possible for good fit. 
			// We work out this distance later, which will be a bit greater.

			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, mainSizes.getWidth(), mainSizes.getHeight());
		
			// Draw hearts...	    
			DrawHearts drawHearts = new DrawHearts(g2d, mainSizes, closestDistance, hd);	      
			drawHearts.draw();
			//for testing so see where bounding rectangles are...
			//dt.drawTextBoundingRectangles();
			dt.draw();
	    
			g2d.dispose();

			File file = new File("HeartsvvValentine.png");
			ImageIO.write(bufferedImage, "png", file);
	
			file = new File("HeartsvvvValentine.jpg");
			ImageIO.write(bufferedImage, "jpg", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private final TextFormattingDetails tfd;
	private BufferedImage bufferedImage;
	private Graphics2D g2d;	
	private HeartDetails hd;
	private DrawText dt;
	private int pixelTxtLen;	
	private MainSizes mainSizes;
	private final int maxIterations = 300;
	private final int stepIncrement = 4;
}
