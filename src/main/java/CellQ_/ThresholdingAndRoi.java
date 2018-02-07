package CellQ_;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.GaussianBlur;
import ij.process.ImageProcessor;

public class ThresholdingAndRoi {

	public static ImagePlus thresholdImage(ImagePlus impMask) {
		ImagePlus impOut = impMask.duplicate(); // duplicate the original image
		ImageProcessor ip = impOut.getProcessor();
		GaussianBlur gb = new GaussianBlur();
		BackgroundSubtracter bs = new BackgroundSubtracter();
		ImageCalculator ic = new ImageCalculator();
		ContrastEnhancer ce = new ContrastEnhancer();

		IJ.run(impOut, "Invert LUT", ""); // invert

		bs.subtractBackround(ip, 50); // subtractBackround
		gb.blurGaussian(ip, 5, 5, 10); // sigma = 10; needed: doubleX, doubleY,
										// double accuracy
		impOut = ic.run("Divide create 32-bit", impOut, impOut); // divide the
		ce.stretchHistogram(impOut, 0.35); // double saturated = 0.35
		impOut.setProcessor(null, ip.convertToByte(true));
		impOut.setCalibration(impOut.getCalibration()); // update calibration
		Auto_Local_Threshold.Phansalkar(impOut, 15, 0, 0, false);

		IJ.run(impOut, "Make Binary", "");
		IJ.run(impOut, "Despeckle", "");
		IJ.run(impOut, "Watershed", "");
		// Watershed_Algorithm alg = new Watershed_Algorithm();
		// alg.run(impOut.getProcessor());
		// impOut = alg.getOutput();

		return impOut;
	}

	public static void createRoi(ImagePlus impOut) {
		IJ.run(impOut, "Analyze Particles...", "size=20.74-414.74 circularity=0.50-1.00 exclude include add");
	}

}
