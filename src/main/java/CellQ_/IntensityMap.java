package CellQ_;

import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class IntensityMap {

	/**
	 * @param List:
	 *            ArrayList<double[]>, the ArrayList representing the different
	 *            Rois/Particules, the Array the values of the Roi over the time
	 * @param maximumvalue:
	 *            the intensity the brightes pixel should have. For 8bit
	 *            Greyscale it is 255
	 * @param flashPosition:
	 *            the Index of the frame containing the flash (can be obtained
	 *            by FindFlash method)
	 * @return normed list: ArrayList of int[], the minimum intensity is zero,
	 *         the maximum is maximumvalue, intensities in between are normed by
	 *         the range of the intensity values, the frame of the flash is set
	 *         to the minium value
	 * @author Sebastian Raßmann
	 * @date 12.01.2018
	 */

	public static ArrayList<int[]> getNormedList(ArrayList<double[]> List, int maximumvalue, int flashPosition) {

		Double max = Double.MIN_VALUE;
		Double min = Double.MAX_VALUE;
		int sizeofstack = List.get(0).length;
		int numberofpoints = List.size();
		for (double[] point : List) {
			for (double value : point) {
				if (value < min) {
					min = value;
				}
			}
		}
		for (int p = 0; p < numberofpoints; p++) {
			List.get(p)[flashPosition] = min;
		}
		for (double[] point : List) {
			for (double value : point) {
				if (value > max) {
					max = value;
				}
			}
		}
		double range = max - min;
		double conversionfactor = maximumvalue / range;
		ArrayList<int[]> normedList = new ArrayList<int[]>(numberofpoints);
		int points, t;
		for (points = 0; points < numberofpoints; points++) {
			int[] point = new int[sizeofstack];
			for (t = 0; t < sizeofstack; t++) {
				double pixelvalue = List.get(points)[t];
				pixelvalue -= min;
				pixelvalue = pixelvalue * conversionfactor;
				point[t] = (int) Math.round(pixelvalue);
			}
			normedList.add(point);
		}

		return normedList;

	}

	/**
	 * @param List:
	 *            ArrayList<double[]>, the ArrayList representing the different
	 *            Rois/Particules, the Array the values of the Roi over the
	 *            time)
	 * @return normed list: ArrayList of int[], the minim intensity is zero, the
	 *         maximum is maximumvalue, intensities in between are normed by the
	 *         range of the intensity values, the frame of the flash is set to
	 *         the minium value, sets flashposition to 0 and maxvalue to 255
	 * @author Sebastian Raßmann
	 * @date 12.01.2018
	 */

	public static ArrayList<int[]> getNormed8BitGreyscaleList(ArrayList<double[]> sortedList) {
		ArrayList<int[]> list = getNormedList(sortedList, 255, 0);
		return list;
	}

	/**
	 * @param normedList:
	 *            ArrayList<int[]>, the ArrayList representing the different
	 *            Rois/Particules, the Array the values of the Roi over the
	 *            time, for better contrast it is recommended to norm the list
	 *            to values between 0 and up to 255
	 * @param title:
	 *            the image's name
	 * @param width:
	 *            user can insert the desired width of the representing point in
	 *            the resulting image for one cell
	 * @param height:
	 *            user can insert the desired height of the representing point
	 *            in the resulting image for one cell
	 * @return imp: x-axis representing the stacks, y-axis the different points,
	 *         their relative intensities are represented by the brightness
	 * @author Sebastian Raßmann
	 * @date 12.01.2018
	 */

	public static ImagePlus createIntensityMapFromSorted(ArrayList<int[]> normedList, String title, int widthofpoint,
			int heightofpoint) {
		int width = normedList.get(0).length;
		int heigth = normedList.size();
		int pixelvalue = 0;
		int x = 0;
		int y = 0;
		int t, t1, points, point1;
		ImagePlus imp = IJ.createImage(title, "8-bit black", widthofpoint * width, heightofpoint * heigth, 1);
		ImageProcessor ip = imp.getProcessor();
		imp.show();
		for (points = 0; points < heigth; points++) {
			for (t = 0; t < width; t++) {
				pixelvalue = normedList.get(points)[t];
				for (t1 = 0; t1 <= widthofpoint; t1++) {
					for (point1 = 0; point1 <= heightofpoint; point1++) {
						x = t * widthofpoint + t1;
						y = points * heightofpoint + point1;
						ip.putPixel(x, y, pixelvalue);

					}
				}

			}
		}
		return imp;

	}

	public static ImagePlus createIntensityMap(ArrayList<double[]> List, String title, String path, boolean save,
			int widthofpoint, int heightofpoint, int maximumvalue, int flashPosition) {
		ArrayList<int[]> normedList = IntensityMap.getNormedList(List, maximumvalue, flashPosition);
		ImagePlus impMap = IntensityMap.createIntensityMapFromSorted(normedList, title, widthofpoint, heightofpoint);
		if (save = true) {
			IJ.saveAs(impMap, "Tiff", "D:\\TestData\\20170927 AC3 10 nM Iso_001\\" + title + ".tif");
		}
		return impMap;
	}

}
