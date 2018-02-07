package CellQ_;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class CalibrationBar {

	public static void setCalibrationBar(ImagePlus imp, int x, int y) {
		imp = setBase(imp, x, y);
		imp = setScale(imp, x + 14, y + 10);
		// ImageProcessor ip = imp.getProcessor();
		// ip.drawString(Double.toString(ip.getMin()), x+27, y+20, Color.black);
		// TextRoi tr = new TextRoi(x, y, imp);
		// tr.addChar((char)ip.getMin());
	}

	public static ImagePlus setBase(ImagePlus imp, int x, int y) {
		int width = 65;
		int heigth = 140;
		int i;
		int j;

		ImageProcessor ip = imp.getProcessor();

		for (i = y; i < y + heigth; i++)// for y pixels
		{
			for (j = x; j < x + width; j++)// for x pixels
			{
				ip.putPixelValue(j, i, ip.maxValue());
			}
		}

		return imp;
	}

	public static ImagePlus setScale(ImagePlus imp, int x, int y) {
		int width = 13;
		int heigth = 120;
		int i;
		int j;

		ImageProcessor ip = imp.getProcessor();

		double start = 255;
		double steps = 255 / heigth;

		for (i = y; i < y + heigth; i++)// for y pixels
		{
			for (j = x; j < x + width; j++)// for x pixels
			{
				ip.putPixelValue(j, i, start);
			}
			start = start - steps; // decrease start
		}

		return imp;
	}

}
