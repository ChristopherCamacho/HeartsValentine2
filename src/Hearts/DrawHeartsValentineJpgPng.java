package Hearts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import Hyphens.HyphenatePatternsLanguage;

public class DrawHeartsValentineJpgPng {
	
	public static void main(String[] args) {
		
			java.awt.GraphicsEnvironment ge = 
	                java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font[] fonts = ge.getAllFonts(); 
		
		
		
	//	TextFormattingDetails tfd = new TextFormattingDetails(System.getenv("INPUT_PATH_POPPINS"), true, true, HyphenatePatternsLanguage.EnglishUs, 50, 150, 0);
		TextFormattingDetails tfd = new TextFormattingDetails(System.getenv("INPUT_PATH_TAMISE"), true, true, HyphenatePatternsLanguage.French, 50, 150, 10);
		// for excess space in Il at top right
	//	TextFormattingDetails tfd = new TextFormattingDetails(System.getenv("INPUT_PATH_TAMISE"), true, true, HyphenatePatternsLanguage.EnglishBritish, 50, 170, 10);
	//	TextFormattingDetails tfd = new TextFormattingDetails(System.getenv("INPUT_PATH_SILLY_MESSAGE"), true, false, HyphenatePatternsLanguage.EnglishUs, 50, 170, 20);

	//	TextFormattingDetails tfd = new TextFormattingDetails(System.getenv("INPUT_PATH_USELESS_WANKER_LETTER"), true, true, HyphenatePatternsLanguage.EnglishBritish, 50, 150, 0);
		
	 //   TextFormattingDetails tfd = new TextFormattingDetails(System.getenv("INPUT_PATH_TECH_TEST"), true, true, HyphenatePatternsLanguage.EnglishBritish, 50, 150, 0);		
	 //   TextFormattingDetails tfd = new TextFormattingDetails(System.getenv("INPUT_PATH_MANU"), true, true, HyphenatePatternsLanguage.EnglishBritish, 50, 150, 50);		
	  //  TextFormattingDetails tfd = new TextFormattingDetails(System.getenv("INPUT_PATH_MANU_SHORT"), true, false, HyphenatePatternsLanguage.EnglishBritish, 50, 150, 0);		
	 //   TextFormattingDetails tfd = new TextFormattingDetails(System.getenv("INPUT_PATH_MANU_SHORT"), true, true, HyphenatePatternsLanguage.EnglishBritish, 50, 150, 0);		
	
	//	TextFormattingDetails tfd = new TextFormattingDetails(System.getenv("INPUT_PATH_ICH_LIEBE"), true, true, HyphenatePatternsLanguage.German, 50, 150, 0);		

	    DrawHeartsValentine heartsValentine = new DrawHeartsValentine(tfd);	
		
		heartsValentine.computeTextFit();
		heartsValentine.draw(); 
		
	//	heartsValentine.drawTestForCompareSwiftDelete();
	}
}
