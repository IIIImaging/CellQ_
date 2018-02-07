package CellQ_;

import java.awt.Rectangle;
import java.util.ArrayList;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

public class RoiProcessing {

	/**
	 * @param imp:
	 *            thresholded image
	 * @author Leyla Weyermann
	 * @date 07.12.2017
	 */
	public static void createParticleRois(ImagePlus imp) {
		IJ.run(imp, "Set Scale...", "distance=0 known=0 pixel=1 unit=pixel");
		IJ.run(imp, "Analyze Particles...", "size=50-1000 circularity=0.50-1.00 exclude include add");
	}

	/**
	 * @param impOut:
	 *            image mask
	 * @return roiPoints: ArrayList containing objects of type IntensityPoint
	 *         within the separate rois
	 * @author Leyla Weyermann
	 * @date 07.12.2017
	 */
	public static ArrayList<ArrayList<IntensityPoint>> getRoiArrayList(ImagePlus impOut) {

		// instantiate RoiManager
		RoiManager rm = RoiManager.getInstance();
		if (rm == null)
			rm = new RoiManager();
		rm.runCommand("reset");
		// createParticleRois(impOut);

		ThresholdingAndRoi.createRoi(impOut);

		Roi roi;
		Rectangle bound;

		// create Array with number of Rois
		int nRois = rm.getCount();

		// set length of outer ArrayList to value of nRois
		ArrayList<ArrayList<IntensityPoint>> roiPoints = new ArrayList<ArrayList<IntensityPoint>>(nRois);

		int counter;
		for (int i = 0; i < nRois; i++) {
			roi = rm.getRoi(i);
			bound = roi.getBounds();

			// count number of passes
			counter = 0;
			for (int x = (int) Math.round(bound.getX()); x <= (int) Math.round(bound.getX() + bound.getWidth()); x++) {
				for (int y = (int) Math.round(bound.getY()); y <= (int) Math
						.round(bound.getY() + bound.getHeight()); y++) {
					if (roi.contains(x, y)) {
						counter++;
					}
				}
			}

			// set length of inner ArrayList to value of counter
			ArrayList<IntensityPoint> points = new ArrayList<IntensityPoint>(counter);

			// fill inner ArrayList with objects of type IntensityPoint
			for (int x = (int) Math.round(bound.getX()); x <= (int) Math.round(bound.getX() + bound.getWidth()); x++) {
				for (int y = (int) Math.round(bound.getY()); y <= (int) Math
						.round(bound.getY() + bound.getHeight()); y++) {
					if (roi.contains(x, y)) {
						points.add(new IntensityPoint(x, y));
					}
				}
			}
			// fill outer ArrayList with inner ArrayList
			roiPoints.add(points);

		}

		// how to access an object out of the ArrayList
		// int x;
		// for(int i = 0; i < nRois; i++){
		// for(int j = 0; j < roiPoints.get(i).size(); j++){
		// x = roiPoints.get(i).get(j).x;
		// }
		// }
		return roiPoints;
	}

	/**
	 * @param impOut:
	 *            image mask
	 * @param impIn:
	 *            original image
	 * @return roiSize: array with length: number of rois; content: number of
	 *         pixels within the separate rois
	 * @author Leyla Weyermann
	 * @date 13.12.2017
	 */
	public static int[] getRoiSize(ImagePlus impIn, ImagePlus impOut) {
		// get ArrayList
		ArrayList<ArrayList<IntensityPoint>> roiPoints = getRoiArrayList(impOut);
		// create Array
		int[] roiSize;
		int length = roiPoints.size();
		roiSize = new int[length];
		// define variable
		int pointCounter;

		for (int i = 0; i < length; i++) {
			pointCounter = 0;
			for (int j = 0; j < roiPoints.get(i).size(); j++) {
				pointCounter++;
			}
			roiSize[i] = pointCounter; // add value to array
		}
		return roiSize;
	}

	/**
	 * @param impOut:
	 *            image mask
	 * @param impIn:
	 *            original image
	 * @return numberOfRois: Integer, number of Rois
	 * @author Leyla Weyermann
	 * @date 13.12.2017
	 */

	public static int countRois(ImagePlus impOut) {
		// get ArrayList
		ArrayList<ArrayList<IntensityPoint>> roiPoints = getRoiArrayList(impOut);
		int numberOfRois = roiPoints.size();
		return numberOfRois;
	}

}
