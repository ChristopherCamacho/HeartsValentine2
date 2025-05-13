package Hearts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class DrawHearts {
	DrawHearts(Graphics2D g2d, MainSizes mainSizes, int closestDistance, HeartDetails hd) {
		this.g2d = g2d;
		this.mainSizes = mainSizes;
		this.closestDistance = closestDistance; 
		this.hd = hd;
	}
	
	void computeSideHearts(int totalHeartCount, double distance, double startAngle, double beta, List<Point2D> heartsLst, 
			Point2D ptCircleCentre, boolean left) {
		double gamma = 2 * Math.asin(distance / (2 * mainSizes.getRadius())); // seems correct
		int heartCount = 0;
		double angle = left? startAngle : -startAngle + Math.PI;

		if (left) {
			while (angle < beta) {
				heartsLst.add(new Point2D.Double(ptCircleCentre.getX() + mainSizes.getRadius() * Math.cos(angle) - hd.getHeartCenterX(),  ptCircleCentre.getY() - mainSizes.getRadius() * Math.sin(angle) -  hd.getHeartCenterY()));
				angle += gamma;
				heartCount++;
			}
		}
		else {
			while (angle > -beta + Math.PI) {
				heartsLst.add(new Point2D.Double(ptCircleCentre.getX() + mainSizes.getRadius() * Math.cos(angle) -  hd.getHeartCenterX(),  ptCircleCentre.getY() - mainSizes.getRadius() * Math.sin(angle) -  hd.getHeartCenterY()));
				angle -= gamma;
				heartCount++;
			}	
		}
		
		double a = ptCircleCentre.getX() + mainSizes.getRadius() * Math.cos( (left)? beta : Math.PI - beta) -  hd.getHeartCenterX();
		double b = ptCircleCentre.getY() - mainSizes.getRadius() * Math.sin( (left)? beta : Math.PI - beta) -  hd.getHeartCenterY();
		double c = mainSizes.getWidth()/2 -  hd.getHeartCenterX();
		double d = mainSizes.getHeight() - mainSizes.getMargin();

		Point2D ptLast = heartsLst.get(heartsLst.size() - 1);

		// equation of a line through (a, b) and (c, d) is:
		// x + aa*y + bb = 0 where:
		// aa = (c - a)/(b - d) and bb = (ad - bc)/(b - d)
		double aa = (c - a)/(b - d);
		double bb = (a*d - b*c)/(b - d);
		// see link for calculations below
		// https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line

		double distanceFromLine = Math.abs(ptLast.getX() + aa*ptLast.getY() + bb)/Math.sqrt(1 + aa*aa);
		
		double junctionX = (aa * (aa * ptLast.getX() - ptLast.getY()) - bb)/(1 + aa*aa);
		double junctionY = ( - aa * ptLast.getX() + ptLast.getY() - aa * bb)/(1 + aa*aa);
		
		double shortDist = Math.sqrt(distance*distance - distanceFromLine*distanceFromLine);
		
		double zeta = Math.atan (-1/aa) + ((left)? 0 : Math.PI);
		
		double lineX = junctionX + shortDist * Math.cos(zeta);
		double lineY = junctionY + shortDist * Math.sin(zeta);

		heartsLst.add(new Point2D.Double( lineX,   lineY));
		
		while (heartCount <= totalHeartCount ) {
			lineX += distance * Math.cos(zeta);
			lineY += distance * Math.sin(zeta);
			heartsLst.add(new Point2D.Double(lineX,  lineY));
			heartCount++;
		}			
	}

	void draw() {
		g2d.setColor(Color.red);
		List<Point2D> heartsLst = new ArrayList<Point2D>();
				
		// left circle
		Point2D ptLeftTopCircleCentre = new Point2D.Double(mainSizes.getMargin() + mainSizes.getRadius() +  hd.getHeartCenterX(), mainSizes.getMargin() + mainSizes.getRadius() -  hd.getHeartCenterY());
		
		// for test
	//	g2d.drawString("♦", (float) (mainSizes.getWidth()/2 - heartCenterX), (float)ptLeftTopCircleCentre.getY()); // correct
	//	g2d.drawString(hearts, (float) (mainSizes.getWidth()/2 - heartCenterX),  mainSizes.getHeight() - mainSizes.getMargin());  // correct
	//	g2d.drawString("♦", (float) (ptLeftTopCircleCentre.getX()), (float)ptLeftTopCircleCentre.getY());
		
		double startAngle = Math.acos((mainSizes.getWidth()/2.0 - ptLeftTopCircleCentre.getX())/mainSizes.getRadius());
		
////////////////////////////////
		
		double vertDistBottomPt = mainSizes.getHeight() - 2 * mainSizes.getMargin() - mainSizes.getRadius(); // y-coord top circle centres - y coord of bottom of heart
		double alpha = Math.atan(vertDistBottomPt/mainSizes.getRadius());
		double phi = Math.atan(vertDistBottomPt/(mainSizes.getRadius()*Math.cos(startAngle)));

		double beta = 2*Math.PI - alpha - phi;
		double circleDist = (beta  - startAngle)*mainSizes.getRadius();
		double totDistance = circleDist + vertDistBottomPt;

		// first approximation of distance using linear length
		int totalHeartCount = 1;
		double distance = 0;

		do
		{
			distance = totDistance/++totalHeartCount;
		}
		while (distance > closestDistance);
		
		distance = totDistance/--totalHeartCount;
		
		// distance is approximate - between last heart on curve and first on line, we are assuming distance as section of curve 
		// and line when it should be just a line.
		
		// following approximations using numerical computation of line...		
		// prevError is there to prevent hanging in scenario where error margin doesn't reduce 
		double error = 100000000, prevError;
	
		do {
			computeSideHearts(totalHeartCount, distance, startAngle, beta, heartsLst, ptLeftTopCircleCentre, true);
			Point2D ptLast = heartsLst.get(heartsLst.size() - 1);
			prevError = error;
			error = Math.sqrt(Math.pow((mainSizes.getWidth()/2 -  hd.getHeartCenterX()) - ptLast.getX(), 2) + Math.pow((mainSizes.getHeight() - mainSizes.getMargin()) - ptLast.getY(), 2));

			if (error > 1 && error < prevError) {
				distance += (mainSizes.getHeight() - mainSizes.getMargin() > ptLast.getY())? error/totalHeartCount: -error/totalHeartCount;
				heartsLst.clear();
			}
		}  while (error > 1 && error < prevError);
			
		// right circle
		Point2D ptRightTopCircleCentre = new Point2D.Double(mainSizes.getWidth() - mainSizes.getMargin() - mainSizes.getRadius() -  hd.getHeartCenterX(), mainSizes.getMargin() + mainSizes.getRadius() -  hd.getHeartCenterY());
		
		computeSideHearts(totalHeartCount, distance, startAngle, beta, heartsLst, ptRightTopCircleCentre, false); 
	
		for (Point2D pt2d : heartsLst) {
			/*
			Color col = g2d.getColor();
			g2d.setColor(new Color(0, 255, 255));
			// below is bounding square as in Android
		//	g2d.drawRect( (int)(pt2d.getX() - hd.getHeartCenterX()), (int)(pt2d.getY()- hd.getHeartCenterY()), hd.getHeartWidth(), hd.getHeartHeight());
			// below is almost correct bounding square
			g2d.drawRect( (int)(pt2d.getX()), (int)(pt2d.getY()- hd.getHeartHeight()), hd.getHeartWidth(), hd.getHeartHeight());
			g2d.setColor(col); 
			*/
			g2d.drawString(hd.getHeart(), (float)pt2d.getX(), (float)pt2d.getY());
		}
	}

	MainSizes mainSizes;
	private final int closestDistance;  
	private final Graphics2D g2d;
	HeartDetails hd;
}
