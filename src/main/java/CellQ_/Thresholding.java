package CellQ_;

import ij.IJ;
import ij.ImagePlus;

public class Thresholding {

	/**
	 * @param impMask:
	 *            original image
	 * @return impOut: image mask, edited with various filters
	 * @author Leyla Weyermann
	 * @date 07.12.2017
	 */
	public ImagePlus setThreshold(ImagePlus impMask) {
		ImagePlus impOut = impMask.duplicate();

		// IJ.setAutoThreshold(impOut, "Triangle dark");
		// Prefs.blackBackground = true;
		// IJ.run(impOut, "Convert to Mask", "");
		impOut = thresholdImage(impOut, "Triangle");
		IJ.run(impOut, "Invert LUT", "");

		IJ.run(impOut, "Despeckle", "");
		// IJ.run(impOut, "Fill Holes", "");

		IJ.run(impOut, "Watershed", "");
		// Watershed_Algorithm alg = new Watershed_Algorithm();
		// alg.run(impOut.getProcessor());
		// impOut = alg.getOutput();

		return impOut;
	}

	/**
	 * @param imp:
	 *            original image
	 * @param algorithm:
	 *            specific filter of ImageJ
	 * @return impOut: image mask, thresholded, converted to 8 bit grayscale
	 * @author Leyla Weyermann
	 * @date 14.12.2017
	 */

	private static ImagePlus thresholdImage(ImagePlus imp, String algorithm) {
		IJ.setAutoThreshold(imp, (algorithm + " dark"));
		double minThreshold = imp.getProcessor().getMinThreshold();

		ImagePlus impOut = IJ.createHyperStack(imp.getTitle(), imp.getWidth(), imp.getHeight(), imp.getNChannels(),
				imp.getNSlices(), imp.getNFrames(), 8);
		// double imageMax = Math.pow(2.0,8)-1.0;
		double imageMax = 255;
		for (int x = 0; x < imp.getWidth(); x++) {
			for (int y = 0; y < imp.getHeight(); y++) {
				for (int z = 0; z < imp.getStackSize(); z++) {
					if (imp.getStack().getVoxel(x, y, z) >= minThreshold) {
						impOut.getStack().setVoxel(x, y, z, imageMax);
					} else {
						imp.getStack().setVoxel(x, y, z, 0.0);
					}
				}
			}
		}
		// int type = imp.getType();
		// if(type != 8){
		// new StackConverter(imp).convertToGray8();
		// }
		return impOut;
	}
}
