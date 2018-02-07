package CellQ_;

import java.util.ArrayList;
import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class ProcessingImage {

	/**
	 * @param roiPoints:
	 *            ArrayList containing objects of type IntensityPoint within the
	 *            separate rois
	 * @param impOut:
	 *            image mask
	 * @param impIn:
	 *            image to be measured
	 * @param Interger:
	 *            t
	 * @return averageArray: array with length: number of rois; content: average
	 *         of intensity within the separate rois
	 * @author Leyla Weyermann
	 * @date 13.12.2017
	 */
	public static double[] createAverage(ArrayList<ArrayList<IntensityPoint>> roiPoints, ImagePlus impIn, Integer t,
			int flashposition) {

		float meanRoiCalc;
		int pointCounter;
		int x;
		int y;
		double[] averageArray;

		int length = roiPoints.size(); // get number of cells by measuring size
										// of ArrayList (25)
		averageArray = new double[length]; // set length of array to number of
											// cells (length)

		if (t != 0) {
			impIn.setSlice(t); // set specific slice (--> t)
			ImageStack stack = impIn.getStack();
			ImageProcessor ip = stack.getProcessor(t); // get processor of
														// specified slice

			for (int i = 0; i < flashposition; i++)// for number of cells/rois
													// (length of array, 25)
			{
				meanRoiCalc = 0;
				for (int j = 0; j < roiPoints.get(i).size(); j++) // for number
																	// of pixels
																	// within a
																	// specific
																	// roi
				{
					x = roiPoints.get(i).get(j).x; // access to specific object
													// of class IntensityPoint
													// (i: cell/roi, j: pixel)
					y = roiPoints.get(i).get(j).y; // and get coordinates
					meanRoiCalc += ip.getPixelValue(x, y); // sum up pixel
															// values -->
															// meanRoiCalc
				}

				meanRoiCalc /= flashposition; // divide result (meanRoiCalc) by
												// number of pixels
												// (pointCounter)
				averageArray[i] = meanRoiCalc; // add value to array (for each
												// cell)
			}
		} else {
			ImageProcessor ip = impIn.getProcessor();

			for (int i = 0; i < length; i++) // for number of cells/rois (length
												// of array, 25)
			{
				pointCounter = 0;
				meanRoiCalc = 0;
				for (int j = 0; j < roiPoints.get(i).size(); j++) // for number
																	// of pixels
																	// within a
																	// specific
																	// roi
				{
					x = roiPoints.get(i).get(j).x; // access to specific object
													// of class IntensityPoint
													// (i: cell/roi, j: pixel)
					y = roiPoints.get(i).get(j).y; // and get coordinates
					meanRoiCalc += ip.getPixelValue(x, y); // sum up pixel
															// values -->
															// meanRoiCalc
					pointCounter++; // count the pixels by number of passes

				}

				meanRoiCalc /= pointCounter; // divide result (meanRoiCalc) by
												// number of pixels
												// (pointCounter)
				averageArray[i] = meanRoiCalc; // add value to array (for each
												// cell)
			}
		}

		return averageArray;
	}

	/**
	 * @param impOut:
	 *            image mask
	 * @param impFluo:
	 *            original stack
	 * @return stackAverage: ArrayList, double[] within average of every single
	 *         roi for each slice
	 * @author Leyla Weyermann
	 * @date 14.12.2017
	 */
	public static ArrayList<double[]> getStackAverage(ArrayList<ArrayList<IntensityPoint>> roiPoints, ImagePlus impOut,
			ImagePlus impFluo, int flashposition) {

		// declaration
		int t;
		double[] average;

		int numberOfStacks = impFluo.getStack().getSize(); // get number of
															// stacks
		// IJ.log("numberOfStacks = " + numberOfStacks);
		ArrayList<double[]> stackAverage = new ArrayList<double[]>(numberOfStacks); // set
																					// length
																					// of
																					// ArrayList
																					// to
																					// number
																					// of
																					// stacks

		// create average for every single roi, fill double[] with averages, add
		// to ArrayList
		for (t = 1; t <= numberOfStacks; t++) { // for number of stacks,
												// ATTENTION: stack starts with
												// 1!
			average = createAverage(roiPoints, impFluo, t, flashposition); // create
																			// average
																			// array
			stackAverage.add(average); // add array to list
		}
		return stackAverage;
	}

	/**
	 * @param list:
	 *            ArrayList
	 * @author Leyla Weyermann
	 * @date 21.12.2017
	 * @comment reads out ArrayList, size: number of stacks, length: number of
	 *          cells
	 */
	public static void readOutArrayList(ArrayList<double[]> list) {

		// declaration
		int i;
		int j;
		double[] results;
		IJ.log("list.size() " + list.size());
		IJ.log("_________________________________________");
		IJ.log("results.length " + list.get(1).length);
		IJ.log("_________________________________________");
		// read out

		for (i = 0; i < list.size(); i++) { // for number of stacks (size of
											// list, 180)
			results = list.get(i); // get array from list (length of array: 25)
			for (j = 0; j < results.length; j++) { // for number of cells
													// (length of array, 25)
				IJ.log("results(" + (i + 1) + ") = " + results[j]); // show:
																	// results(<number
																	// of
																	// stack>) =
																	// <value of
																	// specific
																	// cell>
			}
		}
	}

	/**
	 * @param list:
	 *            ArrayList
	 * @author Leyla Weyermann
	 * @date 21.12.2017
	 * @comment reads out ArrayList, size: number of cells, length: number of
	 *          stacks
	 */
	public static void readOutArrayList2(ArrayList<double[]> list) {

		int i;
		int j;
		int size = list.size();
		int length = list.get(0).length;

		IJ.log("list.size(): " + size);
		IJ.log("list.get(0).length: " + length);

		for (i = 0; i < list.size(); i++) // for the number of cells (size of
											// list, 25)
		{
			for (j = 0; j < length; j++) // for the number of stacks (length of
											// each array within ArrayList, 180)
			{
				IJ.log("list.get(" + i + ")[" + j + "] = " + list.get(i)[j]); // show:
																				// list.get(i)[j],
																				// i
																				// =
																				// cell,
																				// j
																				// =
																				// time
			}
		}
	}

	/**
	 * @param standardizedList:
	 *            ArrayList, intensity averages after setting baseline
	 * @param sumUp:
	 *            Integer, number of values to sum up
	 * @return slidingWindow: ArrayList, size: number of cells, length: number
	 *         of stacks - (values to sum up decreased by 1), content: summed up
	 *         values
	 * @author Leyla Weyermann
	 * @date 02.01.2018
	 */
	public static ArrayList<double[]> slidingWindow(ArrayList<double[]> standardizedList, Integer sumUp) {

		// declaration
		int i;
		int j;
		int k;
		double outcome;
		// length = length of standardizedList - number of values to sum up
		// "sumUp" decreased by 1
		// last sumUp-1 values can not be calculated
		int length = standardizedList.get(0).length - (sumUp - 1);

		// set size of arrayList
		ArrayList<double[]> listOut = new ArrayList<double[]>(standardizedList.size()); // set
																						// size
																						// of
																						// new
																						// arrayList
																						// to
																						// number
																						// of
																						// cells
																						// (25)

		for (i = 0; i < standardizedList.size(); i++) { // number of cells
			double[] results = new double[length];
			for (j = 0; j < length; j++) { // number of slices
				outcome = 0; // set outcome to 0
				for (k = 0; k < sumUp; k++) { // number of values to sum up
					outcome += standardizedList.get(i)[j + k]; // building
																// outcome by
																// adding up
																// values in
																// array (cell
																// i) of
																// arrayList
																// (standardizedList),
																// sumUp times
				}
				results[j] = outcome / sumUp; // set value at index j to outcome
			}

			listOut.add(results);

		}
		return listOut;

	}

	/**
	 * @param list:
	 *            ArrayList
	 * @param preSignal:
	 *            Integer, number of times before signal
	 * @param cell:
	 *            Integer, specific cell
	 * @return average: Double, average of intensity within specific time period
	 * @author Leyla Weyermann
	 * @date 14.12.2017
	 */
	public static double setBaseline(ArrayList<double[]> list, Integer preSignal, Integer cell) {
		int t;
		double signal = 0;
		double average;
		for (t = 0; t < preSignal; t++) // for the set number of times (=
										// preSignal)
		{
			signal += list.get(t)[cell]; // sum up the values of a specific cell
											// at certain times
		}
		average = signal / preSignal; // divide the result (signal) by number of
										// times (preSignal) --> create average
		return average;
	}

	/**
	 * @param list:
	 *            ArrayList
	 * @param preSignal:
	 *            Integer, number of times before signal
	 * @return standardizedValues: ArrayList with standardized values
	 * @author Leyla Weyermann
	 * @date 02.01.2018
	 */
	public static ArrayList<double[]> standardizeValues(ArrayList<double[]> list, Integer preSignal) {
		int t;
		int cell;
		double average;
		double[] cellSignal = new double[list.size()]; // set length of array
														// (cellSignal) to
														// number of stacks
														// (180);
		ArrayList<double[]> standardizedList = new ArrayList<double[]>(list.get(0).length); // set
																							// size
																							// of
																							// ArrayList
																							// to
																							// number
																							// of
																							// cells
																							// (25)

		// IJ.log("list.size() " + list.size());
		// IJ.log("_________________________________________" );

		for (cell = 0; cell < list.get(0).length; cell++) { // for the number of
															// cells (25)
			average = setBaseline(list, preSignal, cell);// create average array
															// from a certain
															// cell
			// IJ.log("average = " + average);
			// IJ.log("Cell = " + cell);
			cellSignal = new double[list.size()];// create new array
													// "cellSignal" with length:
													// list.size (180)
			for (t = 0; t < list.size(); t++) { // for the number of stacks
												// (180)
				cellSignal[t] = list.get(t)[cell] / average; // divide values of
																// certain cell
																// at certain
																// time by
																// created
																// average -->
																// get array
																// with
																// standardized
																// values
				// IJ.log("cellSignal" + "[" + t + "] = " + cellSignal[t]);
			}
			standardizedList.add(cell, cellSignal);// add standardized array to
													// ArrayList (for each cell)
		}
		return standardizedList;
	}

	/**
	 * @param order:
	 *            original Array
	 * @return order: reversed Array
	 * @author Leyla Weyermann
	 * @date 02.01.2018
	 */
	public static int[] reverseArray(int[] order) {
		int j;
		for (int i = 0; i < order.length / 2; i++) // for the half of array
													// length
		{
			j = order[i]; // set j to value 1 of array

			order[i] = order[order.length - 1 - i]; // set value 2 to position 1

			order[order.length - 1 - i] = j; // set value 1 to position 2
		}
		return order;
	}

	/**
	 * @param roiPoints:
	 *            ArrayList containing objects of type IntensityPoint within the
	 *            separate rois
	 * @param impOut:
	 *            image mask
	 * @param impIn:
	 *            image to be measured
	 * @return order: Array containing the right order
	 * @author Leyla Weyermann
	 * @date 02.01.2018
	 */
	public static int[] sortingArray(ArrayList<ArrayList<IntensityPoint>> roiPoints, ImagePlus impOut, ImagePlus impIn,
			int flashposition) {
		int i;
		int j;
		double[] sortAverage = createAverage(roiPoints, impIn, 0, flashposition); // get
																					// array
																					// of
																					// intensity
																					// average
		Arrays.sort(sortAverage); // sort array
		double[] average = createAverage(roiPoints, impIn, 0, flashposition); // get
																				// array
																				// of
																				// intensity
																				// average
		int[] order = new int[average.length]; // create array with length of
												// average array

		for (i = 0; i < average.length; i++) // for the length of array (number
												// of cells, 25)
		{
			j = 0; // set j back to 0
			while (sortAverage[i] != average[j]) // while the values of the
													// arrays are not equal
			{
				j++; // increase j --> go to next position within array
			}
			order[i] = j; // set position of similarity to array
			reverseArray(order); // reverse the array
		}
		return order;
	}

	/**
	 * @param list:
	 *            ArrayList containing edited intensity values
	 * @param order:
	 *            Array containing the right order
	 * @return listOut: ArrayList in new order
	 * @author Leyla Weyermann
	 * @date 02.01.2018
	 */
	public static ArrayList<double[]> sortedList(ArrayList<double[]> list, int[] order) {
		int i;
		ArrayList<double[]> listOut = new ArrayList<double[]>(list.size()); // create
																			// ArrayList,
																			// size
																			// =
																			// original
																			// size
																			// of
																			// input
																			// ArrayList
																			// (number
																			// of
																			// cells,
																			// 25)
		for (i = 0; i < order.length; i++) // for length of input array (number
											// of cells, 25)
		{
			int[] orderCopy = order; // create new array
			listOut.add(i, list.get(orderCopy[i])); // add value of original
													// ArrayList to new
													// ArrayList, position:
													// value of array at same
													// index
		}
		return listOut;
	}
}
