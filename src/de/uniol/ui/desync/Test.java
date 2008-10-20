package de.uniol.ui.desync;

/**
 * Small utility class which calculates the slope of a straight between two
 * points in two-dimensional space.
 * 
 * @author <a href=
 *         "mailto:Christian%20Hinrichs%20%3Cchristian.hinrichs@uni-oldenburg.de%3E"
 *         >Christian Hinrichs, christian.hinrichs@uni-oldenburg.de</a>
 * 
 */
public class Test {

	public static void main(String[] args) {
		double x1 = 385.0;
		double y1 = 14.5;
		double x2 = 435.0;
		double y2 = 15.5;
		double m = (y2 - y1) / (x2 - x1);
		System.out.println(m);
	}
}
