package CellQ_;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.*;

import javax.swing.UIManager;

import CreatePDF.CreatePdf;
import CreatePDF.PdfHeatMap;
import ij.*;
import ij.gui.*;
import ij.measure.*;
import ij.plugin.*;
import ij.plugin.frame.RoiManager;
import ij.text.*;

public class Main implements PlugIn, Measurements {
	// Name variables
	static final String PLUGINNAME = "CellQ";
	static final String PLUGINVERSION = "0.0.1";
	static final String ENDING = "_CellQ";

	// Fix fonts
	static final Font SUPERHEADINGFONT = new Font("Sansserif", Font.BOLD, 16);
	static final Font HEADINGFONT = new Font("Sansserif", Font.BOLD, 14);
	static final Font SUBHEADINGFONT = new Font("Sansserif", Font.BOLD, 12);
	static final Font TEXTFONT = new Font("Sansserif", Font.PLAIN, 12);
	static final Font INSTRUCTIONSFONT = new Font("Sansserif", 2, 12);
	static final Font ROIFONT = new Font("Sansserif", Font.PLAIN, 20);

	// Fix formats
	DecimalFormat dFormat6 = new DecimalFormat("#0.000000");
	DecimalFormat dFormat3 = new DecimalFormat("#0.000");
	DecimalFormat dFormat0 = new DecimalFormat("#0");
	DecimalFormat dFormatDialog = new DecimalFormat("#0.000000");

	static final String[] NFORMATS = { "US (0.00...)", "Germany (0,00...)" };

	static SimpleDateFormat nameDateFormatter = new SimpleDateFormat("yyMMdd_HHmmss");
	static SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy-MM-dd	HH:mm:ss");
	static SimpleDateFormat fullDateFormatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// Progress Dialog
	ProgressDialog progressDialog;
	boolean processingDone = false;
	boolean continueProcessing = true;

	// -----------------define params for Dialog-----------------
	int tasks = 1;
	boolean saveDate = false;
	boolean flash = true;
	boolean createbundleddoc = true;
	String ChosenNumberFormat = "Germany (0,00...)";
	// //-----------------define params for Dialog-----------------

	public void run(String arg) {
		dFormat6.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		dFormat3.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		dFormat0.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		dFormatDialog.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		// -------------------------GenericDialog--------------------------------------
		// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

		GenericDialog gd = new GenericDialog(PLUGINNAME + " - set parameters");

		// show
		// Dialog-----------------------------------------------------------------
		// .setInsets(top, left, bottom)
		gd.setInsets(0, 0, 0);
		gd.addMessage(PLUGINNAME + ", Version " + PLUGINVERSION + ", \u00a9 2018 JN Hansen & JF Jiklei",
				SUPERHEADINGFONT);
		gd.setInsets(10, 0, 0);
		gd.addMessage("Output settings", SUBHEADINGFONT);
		gd.setInsets(0, 0, 0);
		gd.addCheckbox("save date in output file names", saveDate);
		gd.setInsets(0, 0, 0);
		gd.addCheckbox("automatically flash find", flash);
		gd.setInsets(0, 0, 0);
		gd.addCheckbox("generate Bundled Document", createbundleddoc);
		gd.showDialog();
		// show
		// Dialog-----------------------------------------------------------------

		// read and process
		// variables--------------------------------------------------
		saveDate = gd.getNextBoolean();
		flash = gd.getNextBoolean();
		createbundleddoc = gd.getNextBoolean();
		// read and process
		// variables--------------------------------------------------
		if (gd.wasCanceled())
			return;

		String bundledpdfpath = null;
		if (createbundleddoc == true) {
			GenericDialog ngd = new GenericDialog("Choose filepath to save PDF Bundle");
			// get filepath
			String bundledfilepath;

		}
		//// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		//// ---------------------end-GenericDialog-end----------------------------------
		//// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
		//
		// /**
		// * AUTOMATED FILE OPENING - START
		// * */
		String name[] = { "", "" };
		String dir[] = { "", "" };

		// Improved file selector
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		OpenFilesDialog od = new OpenFilesDialog();
		od.setLocation(0, 0);
		od.setVisible(true);
		od.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(WindowEvent winEvt) {
				return;
			}
		});

		// Waiting for od to be done
		while (od.done == false) {
			try {
				Thread.currentThread().sleep(50);
			} catch (Exception e) {
			}

			tasks = od.filesToOpen.size();
			name = new String[tasks];
			dir = new String[tasks];
			for (int task = 0; task < tasks; task++) {
				name[task] = od.filesToOpen.get(task).getName();
				dir[task] = od.filesToOpen.get(task).getParent() + System.getProperty("file.separator");
			}
		}

		/**
		 * AUTOMATED FILE OPENING - END
		 */

		/**
		 * Start PROGRESS DIALOG
		 */
		progressDialog = new ProgressDialog(name, tasks);
		progressDialog.setLocation(0, 0);
		progressDialog.setVisible(true);
		progressDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				if (processingDone == false) {
					IJ.error("Script stopped...");
				}
				continueProcessing = false;
				return;
			}
		});

		/**
		 * PROCESSING OF MULTIPLE IMAGES
		 */

		for (int task = 0; task < tasks; task++) {
			running: while (continueProcessing) {
				Date startDate = new Date();
				progressDialog.updateBarText("in progress...");
				// Check for problems
				if (name[task].contains(".")
						&& name[task].substring(name[task].lastIndexOf("."), name[task].length()).equals(".txt")) {
					progressDialog.notifyMessage(
							"Task " + (task + 1) + "/" + tasks + ": A file is no image! Could not be processed!",
							ProgressDialog.ERROR);
					progressDialog.moveTask(task);
					break running;
				}
				if (name[task].contains(".")
						&& name[task].substring(name[task].lastIndexOf("."), name[task].length()).equals(".zip")) {
					progressDialog.notifyMessage(
							"Task " + (task + 1) + "/" + tasks + ": A file is no image! Could not be processed!",
							ProgressDialog.ERROR);
					progressDialog.moveTask(task);
					break running;
				}
				// Check for problems

				// Define Output File Names
				Date currentDate = new Date();
				String filePrefix;
				if (name[task].contains(".")) {
					filePrefix = name[task].substring(0, name[task].lastIndexOf(".")) + ENDING;
				} else {
					filePrefix = name[task] + ENDING;
				}

				if (saveDate) {
					filePrefix += "_" + nameDateFormatter.format(currentDate);
				}

				filePrefix = dir[task] + filePrefix;

				File folder = new File(filePrefix);
				folder.mkdirs();

				filePrefix += System.getProperty("file.separator");
				// Define Output File Names

				// /******************************************************************
				// *** PROCESSING OF imp ***
				// *******************************************************************/
				// //TODO insert processing here
				//
				// open images

				RoiManager rm = RoiManager.getInstance();
				if (rm == null) {
					rm = new RoiManager();
				}
				rm.runCommand("reset");

				File mCherry = new File(
						"" + dir[task] + name[task] + System.getProperty("file.separator") + "mcherry.tif");
				File fluo = new File("" + dir[task] + name[task] + System.getProperty("file.separator") + "Fluo4.tif");

				if (mCherry.exists() && fluo.exists()) {

					ImagePlus impMcherry;
					ImagePlus impFluo;
					impMcherry = IJ.openImage(
							"" + dir[task] + name[task] + System.getProperty("file.separator") + "mcherry.tif");
					impFluo = IJ.openImage(
							"" + dir[task] + name[task] + System.getProperty("file.separator") + "Fluo4.tif");

					impMcherry.show();
					impFluo.show();

					// IJ.saveAsTiff(impFluo, filePrefix + "fluo4");

					ImagePlus impIn = impMcherry.duplicate();
					ImagePlus impOut = ThresholdingAndRoi.thresholdImage(impMcherry);
					impIn.show();
					impOut.show();

					int preSignal;
					if (flash == true) // automatically find flash (if flash ==
										// true)
					{
						preSignal = FindFlash.findflash(impFluo) - 1;
					} else {
						preSignal = 24;
					}

					// get average arrayList
					ArrayList<ArrayList<IntensityPoint>> roiPoints = RoiProcessing.getRoiArrayList(impOut);
					ArrayList<double[]> stackAverage;
					stackAverage = ProcessingImage.getStackAverage(roiPoints, impOut, impFluo, preSignal);
					// ProcessingImage.readOutArrayList(stackAverage);

					// sort intensity values (mCherry)
					double[] sortedIntensities = ProcessingImage.createAverage(roiPoints, impIn, 0, preSignal);
					Arrays.sort(sortedIntensities);

					// set baseline

					int cells = roiPoints.size();
					int stacks = impFluo.getStackSize();

					CreatePdf.generateDocument(filePrefix + "results.pdf");
					CreatePdf.createPDFMultipleImageFromStack(impFluo, 3, 24, filePrefix);
					CreatePdf.createPDFMultipleImageFromStackSecondLine(impFluo, 3, 30, impFluo.getStack().getSize(),
							filePrefix);
					CreatePdf.imagesThirdLine(impOut, impIn, impFluo, filePrefix);
					CreatePdf.createTitle(filePrefix,
							"Image Analysis Results - CellQ, Pluginversion: " + PLUGINVERSION);
					CreatePdf.createSubtitle(filePrefix,
							"Number of detected Cells = " + cells + "; Number of Stacks from fluo4 = " + stacks);
					CreatePdf.createDate(filePrefix,
							"Datafile was generated on " + fullDateFormatter2.format(currentDate));
					CreatePdf.createFilePrefix(filePrefix, "folder: " + filePrefix);

					ArrayList<double[]> standardizedList;
					standardizedList = ProcessingImage.standardizeValues(stackAverage, preSignal);
					// ProcessingImage.readOutArrayList2(standardizedList);

					// get upgraded arrayList
					// int sumUp = 3;
					// ArrayList<double[]> slidingWindow =
					// ProcessingImage.slidingWindow(standardizedList, sumUp);
					// ProcessingImage.readOutArrayList2(slidingWindow);

					// get ArrayList sorted depending on mCherry intensities
					int[] sortedArray = ProcessingImage.sortingArray(roiPoints, impOut, impIn, preSignal);
					ArrayList<double[]> sortedList = ProcessingImage.sortedList(standardizedList, sortedArray);
					// ProcessingImage.readOutArrayList2(sortedList);

					// for(double[] arrays : standardizedList){
					// IJ.log("something \n");
					// for(double value: arrays){
					// IJ.log(value + " ");
					// }
					// }

					impIn.changes = false;
					impIn.close();

					impOut.changes = false;
					impOut.close();

					impMcherry.changes = false;
					impMcherry.close();

					impFluo.changes = false;
					impFluo.close();
					/******************************************************************
					 *** OUPUT OPTIONS *** //
					 *******************************************************************/

					//
					// //TODO The Output is not yet defined. Currently we only
					// read the data.
					//

					String maptitle = "newmap";
					float frametime = 1.00000f;
					float starttime = 0;
					float endtime = 180;
					byte showntimepoints = 5;
					boolean createHeatMap = true;
					boolean donotaskagain = false;
					boolean deletePDF = false;
					if (task == 0 || donotaskagain) {
						GenericDialog gdForMap = new GenericDialog("New Heat Map");
						gdForMap.addCheckbox("Create Heat Map", createHeatMap);
						gdForMap.addCheckbox("Do not ask again", donotaskagain);
						gdForMap.addCheckbox("Delete original PDF", deletePDF);
						gdForMap.addStringField("enter mapname", maptitle);
						gdForMap.addNumericField("Insert time between frames (s)         ", frametime, 5);
						gdForMap.addNumericField("Enter startpoint of measurement(s)     ", starttime, 1);
						gdForMap.addNumericField("Enter endpoint of measurement(s)       ", endtime, 5);
						gdForMap.addNumericField("Enter timepoints to be shown in the pdf", showntimepoints, 0);
						gdForMap.showDialog();
						if (gdForMap.wasCanceled())
							return;
						maptitle = gdForMap.getNextString();
						frametime = (float) gdForMap.getNextNumber();
						starttime = (float) gdForMap.getNextNumber();
						endtime = (float) gdForMap.getNextNumber();
						showntimepoints = (byte) gdForMap.getNextNumber();
						createHeatMap = gdForMap.getNextBoolean();
						donotaskagain = gdForMap.getNextBoolean();
						deletePDF = gdForMap.getNextBoolean();
					}

					if (createHeatMap == true) {
						String mappath = filePrefix + maptitle + ".bmp";
						ArrayList<int[]> normedList = IntensityMap.getNormedList(sortedList, 255, preSignal); // normed
																												// list
																												// is
																												// manipulated
																												// Data,
																												// don't
																												// use
																												// for
																												// further
																												// analysis
																												// or
																												// plotting
						ImagePlus impMap = IntensityMap.createIntensityMapFromSorted(normedList, maptitle, 20, 20);
						impMap.show();
						IJ.saveAs(impMap, "BMP", mappath);
						impMap.close();
						String pdfname = "results.pdf";
						String maptargetpath = filePrefix + "results.mapadded.pdf";
						String headline = "Intensity Mapper of the expirement";
						String subtitle = "Folder: " + filePrefix;
						try {
//							int pagewithmap = PdfHeatMap.insertDecriptedImage(filePrefix + pdfname, mappath,
//									maptargetpath, starttime, endtime, preSignal, showntimepoints, sortedIntensities,
//									headline, subtitle, bundledpdfpath);
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (!deletePDF) {
							IJ.log("File will not be deleted!");
						}
						if (deletePDF) {
							boolean success = (new File(filePrefix + pdfname)).delete();
							if (success) {
								IJ.log("deleted");
							}
						}
					}
					if (!createbundleddoc) {
						// ohne heatmap bündeln
					}

					// SAVE IMAGE

					// start metadata file
					TextPanel tp1 = new TextPanel("results");

					tp1.append("Saving date:	" + fullDateFormatter.format(currentDate) + "	Starting date:	"
							+ fullDateFormatter.format(startDate));
					tp1.append("Image name:	" + name[task]);
					tp1.append("");
					tp1.append("Results");

					// TODO
					if (sortedList == null) {
						IJ.log("leer");
					}
					int c;
					int t;
					int numberOfStacks = sortedList.get(0).length;
					int numberOfCells = sortedList.size();

					String appendTxt = "	";

					appendTxt += "Slice" + "	";

					for (c = 0; c < numberOfCells; c++) {
						appendTxt += "Cell " + (c + 1) + "	";
					}
					tp1.append(appendTxt);
					//// appendTxt += " " + "\r\n";
					for (t = 0; t < numberOfStacks; t++) {
						appendTxt = "	" + (t + 1);
						for (c = 0; c < numberOfCells; c++) {
							appendTxt += "	" + dFormat6.format(sortedList.get(c)[t]);
						}
						//// appendTxt += "\r\n";
						tp1.append(appendTxt);
					}

					TextPanel tp2 = new TextPanel("results, binning");

					tp2.append("Saving date:	" + fullDateFormatter.format(currentDate) + "	Starting date:	"
							+ fullDateFormatter.format(startDate));
					tp2.append("Image name:	" + name[task]);
					tp2.append("");
					tp2.append("Results, binning");

					String appendTab = "	" + "Intensity" + "	";

					for (c = 0; c < numberOfCells; c++) {
						appendTab += sortedIntensities[c] + "	";
					}

					tp2.append(appendTab);

					appendTab = "	" + "Group" + "	";
					for (c = 0; c < numberOfCells; c++) {
						if (300 < sortedIntensities[c] && sortedIntensities[c] <= 400) {
							appendTab += "1: 300-400" + "	";
						} else if (400 < sortedIntensities[c] && sortedIntensities[c] <= 500) {
							appendTab += "2: 400-500" + "	";
						} else if (500 < sortedIntensities[c] && sortedIntensities[c] <= 600) {
							appendTab += "3: 500-600" + "	";
						} else if (sortedIntensities[c] > 600) {
							appendTab += "4: >600" + "	";
						}
					}

					tp2.append(appendTab);
					appendTab = "";

					appendTab += "Slice" + "	" + "	";
					for (c = numberOfCells - 1; c >= 0; c--) {
						appendTab += "Cell " + (c + 1) + "	";
					}
					tp2.append(appendTab);

					for (t = 0; t < numberOfStacks; t++) {
						appendTab = (t + 1) + "	";
						for (c = numberOfCells - 1; c >= 0; c--) {
							appendTab += "	" + dFormat6.format(sortedList.get(c)[t]);
						}
						tp2.append(appendTab);
					}

					// Save Metadata text file
					addFooter(tp1, currentDate);
					tp1.saveAs(filePrefix + "results.txt");
					addFooter(tp2, currentDate);
					tp2.saveAs(filePrefix + "sortedResults.txt");

					impMcherry.changes = false;
					impMcherry.close();
					impFluo.changes = false;
					impFluo.close();

					// //Output Datafile
					//
					processingDone = true;

					rm.close();
				}

				break running;
			}
			progressDialog.updateBarText("finished!");
			progressDialog.setBar(1.0);
			progressDialog.moveTask(task);
		}
	

}

	private void addFooter(TextPanel tp, Date currentDate) {
		tp.append("");
		tp.append("Datafile was generated on " + fullDateFormatter2.format(currentDate) + " by '" + PLUGINNAME
				+ "', an ImageJ plug-in created by the  (http://www.iii.uni-bonn.de/wachten_lab/contact.html).");
		tp.append("The plug-in '" + PLUGINNAME + "' is distributed in the hope that it will be useful,"
				+ " but WITHOUT ANY WARRANTY; without even the implied warranty of"
				+ " MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
		tp.append("Plug-in version:	V" + PLUGINVERSION);
	}

}// end main class