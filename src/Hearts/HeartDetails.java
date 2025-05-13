package Hearts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

final class HeartDetails {
	
	HeartDetails(Graphics2D g2d) {
		this.g2d = g2d;
		initialize();
	}
	
	private void initialize() {
	    Font newFont = new Font("TimesRoman", Font.PLAIN, 150);
	    g2d.setFont(newFont);   
	 
	    Rectangle2D rectHeart = g2d.getFontMetrics().getStringBounds(heart, g2d);
	    heartCenterX = rectHeart.getCenterX();
	    heartCenterY = rectHeart.getCenterY();
	      
	    FontRenderContext frc = g2d.getFontRenderContext();
        LineMetrics metrics = newFont.getLineMetrics(heart, frc);
   
        float ascentDescent = metrics.getAscent() - metrics.getDescent();

		int heartX = (int) (rectHeart.getX() + rectHeart.getWidth() * 0.05);  
		heartWidth = (int) (rectHeart.getWidth() * 0.9);
		
		int heartY = (int) (-ascentDescent + ascentDescent * 0.15);  
		heartHeight = (int) (ascentDescent * 0.8);

		// This code draws a rectangle and the bounding rect that should touch edges of heart.
		// g2d.drawString(heart, 150, 150);
		// g2d.drawRect(150 + heartX, 150 + heartY, heartWidth, heartHeight);
	}
	
	double getHeartCenterX() {
		return heartCenterX;
	}
	double getHeartCenterY() {
		return heartCenterY;
	}
	int getHeartWidth() {
		return heartWidth;
	}
	int getHeartHeight() {
		return heartHeight;
	}
	String getHeart() {
		return heart;
	}
	
	private final Graphics2D g2d;
	private final String heart = "♥";
	private double heartCenterX;
	private double heartCenterY;
	private int heartWidth;
	private int heartHeight;
}
