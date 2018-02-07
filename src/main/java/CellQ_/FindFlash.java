package CellQ_;

import ij.ImagePlus;
import ij.measure.Measurements;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

public class FindFlash {

	public static int findflash(ImagePlus imp) {
		/**
		 * Returns the frame position of the flash
		 */
		int flashPosition = 1;
		int bd = imp.getStackSize();
		ImageProcessor ip = imp.getProcessor();
		double meanImageMax = 0;
		for (int z = 1; z <= bd; z++) {
			imp.setSlice(z);
			double meanImage = ImageStatistics.getStatistics(ip, Measurements.MEAN, null).mean;
			if (meanImage > meanImageMax) {
				meanImageMax = meanImage;
				flashPosition = z;
			}
		}
		return flashPosition;
	}

}
