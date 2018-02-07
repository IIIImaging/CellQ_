package CreatePDF;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

//pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import CellQ_.CalibrationBar;
import CellQ_.ThresholdingAndRoi;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.Duplicator;
import ij.plugin.frame.RoiManager;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

public class CreatePdf {

	public static void generateDocument(String filepath) {
		try {
			PDDocument document = new PDDocument(); // create document

			PDPage page = new PDPage();
			document.addPage(page); // add page to document
			document.save(filepath);
			document.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void insertImage(String filepath, String imagepath, Integer x, Integer y) {
		try {
			File file = new File(filepath); // load an existing PDF document
			PDDocument document = PDDocument.load(file);
			PDPage page = document.getPage(0); // get page

			PDImageXObject pdImage = PDImageXObject.createFromFile(imagepath, document); // creating
																							// PDImageXObject
																							// object
																							// (image)
			PDPageContentStream contents = new PDPageContentStream(document, page, AppendMode.APPEND, true, true); // pass
																													// the
																													// document
																													// object
																													// and
																													// the
																													// page
																													// object
			// contents.drawImage(pdImage, x, y, 10, 10);
			contents.drawImage(pdImage, x, y, 10, 10); // insert image at
														// certain position
			contents.close();
			document.save(filepath);
			document.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void writeTitle(PDPageContentStream contentStream, String Title, int xPosition, int yPosition,
			int size) {

		try {
			PDFont font = PDType1Font.HELVETICA_BOLD;

			contentStream.beginText();
			contentStream.setFont(font, size);
			contentStream.newLineAtOffset(xPosition, yPosition);
			contentStream.showText(Title);
			contentStream.endText();
			contentStream.close();
		}

		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void createTitle(String filePrefix, String text) {
		try {
			PDDocument doc = null;
			doc = PDDocument.load(new File(filePrefix + "results.pdf"));
			PDPage page = doc.getPage(0);
			PDRectangle pageSize = page.getMediaBox();
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
			int xPosition = (int) (pageSize.getWidth() / 60);
			int yPosition = (int) ((pageSize.getHeight() - pageSize.getHeight() / 40));
			writeTitle(contentStream, text, xPosition, yPosition, 18);
			contentStream.close();
			doc.save(filePrefix + "results.pdf");
			doc.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	public static void createSubtitle(String filePrefix, String text) {
		try {
			PDDocument doc = null;
			doc = PDDocument.load(new File(filePrefix + "results.pdf"));
			PDPage page = doc.getPage(0);
			PDRectangle pageSize = page.getMediaBox();
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
			int xPosition = (int) (pageSize.getWidth() / 60);
			int yPosition = (int) ((pageSize.getHeight() - pageSize.getHeight() / 20));
			writeTitle(contentStream, text, xPosition, yPosition, 10);
			contentStream.close();
			doc.save(filePrefix + "results.pdf");
			doc.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void createDate(String filePrefix, String currentDate) {
		try {
			PDDocument doc = null;
			doc = PDDocument.load(new File(filePrefix + "results.pdf"));
			PDPage page = doc.getPage(0);
			PDRectangle pageSize = page.getMediaBox();
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
			int xPosition = (int) (pageSize.getWidth() / 60);
			int yPosition = (int) ((pageSize.getHeight() - pageSize.getHeight() / 20));
			writeTitle(contentStream, currentDate, xPosition, yPosition - 15, 10);
			contentStream.close();
			doc.save(filePrefix + "results.pdf");
			doc.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void createFilePrefix(String filePrefix, String text) {
		try {
			PDDocument doc = null;
			doc = PDDocument.load(new File(filePrefix + "results.pdf"));
			PDPage page = doc.getPage(0);
			PDRectangle pageSize = page.getMediaBox();
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
			int xPosition = (int) (pageSize.getWidth() / 60);
			int yPosition = (int) ((pageSize.getHeight() - pageSize.getHeight() / 20));
			writeTitle(contentStream, text, xPosition, yPosition - 30, 10);
			contentStream.close();
			doc.save(filePrefix + "results.pdf");
			doc.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void createPDFFromImageInLine(int numberOfImages, int ImageNumber, int counter, String filePrefix,
			String imagePath) {

		int space = 5;
		int spaceHight = 10;

		try {
			PDDocument doc = null;
			doc = PDDocument.load(new File(filePrefix + "results.pdf"));
			PDPage page = doc.getPage(0);
			PDRectangle pageSize = page.getMediaBox();
			float width = pageSize.getWidth() - pageSize.getWidth() / 10;
			// float hight = pageSize.getHeight()-pageSize.getHeight()/10;
			float xPositionStart = pageSize.getWidth() / 60;
			float yPositionStart = (pageSize.getHeight() - pageSize.getHeight() / 10);

			PDImageXObject pdImage = PDImageXObject.createFromFile(filePrefix + "before" + ImageNumber + ".png", doc);
			// IJ.log("File #" + ImageNumber + "Opend: " + filePrefix +
			// "before"+ImageNumber+ ".png");
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);

			float scale = (width) / (numberOfImages * pdImage.getWidth());
			// IJ.log("scale " +scale);
			// IJ.log("xPositionStart " +xPositionStart);
			// IJ.log("yPositionStart " +yPositionStart);
			// IJ.log("counter " +counter);

			contentStream.drawImage(pdImage,
					xPositionStart + (pdImage.getWidth() * scale) * (counter - 1) + (counter - 1) * space,
					yPositionStart - pdImage.getHeight() * scale, pdImage.getWidth() * scale,
					pdImage.getHeight() * scale);
			writeTitle(contentStream, "fluo4 " + "before" + ImageNumber + ".png",
					(int) (xPositionStart + (pdImage.getWidth() * scale) * (counter - 1) + (counter - 1) * space),
					(int) (yPositionStart - pdImage.getHeight() * scale - spaceHight), 10);

			contentStream.close();
			doc.save(filePrefix + "results.pdf");

			doc.close();
		}

		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void createPDFMultipleImageFromStack(ImagePlus imp, int numberOfImagesBefore, int lastImage,
			String filePrefix) {

		Duplicator dp = new Duplicator();
		ImagePlus impTime;
		CreatePdf.generateDocument(filePrefix + "results.pdf");

		int counter = 0;
		for (int i = (int) lastImage / numberOfImagesBefore; i <= lastImage; i = i
				+ (int) lastImage / numberOfImagesBefore) {
			impTime = dp.run(imp, i, i);
			impTime.show();
			IJ.run(impTime, "Set Scale...", "distance=1.5528 known=1 pixel=1 unit=µm");
			IJ.run(impTime, "Scale Bar...",
					"width=50 height=4 font=14 " + "color=White background=None location=[Lower Right] bold");
			impTime.setDisplayRange(150, 700);
			ImageConverter ic = new ImageConverter(impTime);
			ic.convertToGray8();
			CalibrationBar.setCalibrationBar(impTime, 610, 20);
			// IJ.run(impTime, "Calibration Bar...", "location=[Upper Right]
			// fill=White "
			// + "label=Black number=3 decimal=0 font=12 zoom=1 ");
			// impTime = IJ.getImage();

			counter++;
			// IJ.log("counter " +counter);

			IJ.saveAs(impTime, "PNG", filePrefix + "before" + i);
			CreatePdf.createPDFFromImageInLine(numberOfImagesBefore, i, counter, filePrefix, filePrefix + "before" + i);
			impTime.close();
		}
	}

	public static void createPDFMultipleImageFromStackSecondLine(ImagePlus imp, int numberOfImages, int startImage,
			int lastImage, String filePrefix) {

		Duplicator dp = new Duplicator();
		ImagePlus impTime;

		int counter = 0;
		for (int i = startImage; i <= lastImage; i = i + (int) lastImage / numberOfImages) {
			impTime = dp.run(imp, i, i);
			impTime.show();
			IJ.run(impTime, "Set Scale...", "distance=1.5528 known=1 pixel=1 unit=µm");
			IJ.run(impTime, "Scale Bar...",
					"width=50 height=4 font=14 " + "color=White background=None location=[Lower Right] bold");

			// IJ.run(impTime, "Calibration Bar...", "location=[Upper Right]
			// fill=White "
			// + "label=Black number=3 decimal=0 font=12 zoom=1 ");
			// impTime = IJ.getImage();

			impTime.setDisplayRange(150, 700);
			ImageConverter ic = new ImageConverter(impTime);
			ic.convertToGray8();
			CalibrationBar.setCalibrationBar(impTime, 610, 20);

			counter++;
			// IJ.log("counter " +counter);

			IJ.saveAs(impTime, "PNG", filePrefix + "after" + i);
			CreatePdf.createPDFFromImageInLineSecond(numberOfImages, i, counter, filePrefix);
			impTime.close();
		}
	}

	public static void createPDFFromImageInLineSecond(int numberOfImages, int ImageNumber, int counter,
			String filePrefix) {
		int space = 5;
		int spaceHight = 10;

		try {
			PDDocument doc = null;
			doc = PDDocument.load(new File(filePrefix + "results.pdf"));
			PDPage page = doc.getPage(0);
			PDRectangle pageSize = page.getMediaBox();
			float width = pageSize.getWidth() - pageSize.getWidth() / 10;
			float xPositionStart = pageSize.getWidth() / 60;

			// create object pdImage
			PDImageXObject pdImage = PDImageXObject.createFromFile(filePrefix + "after" + ImageNumber + ".png", doc);
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);

			float scale = (width) / (numberOfImages * pdImage.getWidth());
			float yPositionStart = (pageSize.getHeight() - (pageSize.getHeight() / 10) - pdImage.getHeight() * scale
					- spaceHight - space);

			// IJ.log("scale " +scale);
			// IJ.log("xPositionStart " +xPositionStart);
			// IJ.log("yPositionStart " +yPositionStart);
			// IJ.log("counter " +counter);

			contentStream.drawImage(pdImage,
					xPositionStart + (pdImage.getWidth() * scale) * (counter - 1) + (counter - 1) * space,
					yPositionStart - pdImage.getHeight() * scale, pdImage.getWidth() * scale,
					pdImage.getHeight() * scale);
			writeTitle(contentStream, "fluo4 " + "after" + ImageNumber + ".png",
					(int) (xPositionStart + (pdImage.getWidth() * scale) * (counter - 1) + (counter - 1) * space),
					(int) (yPositionStart - pdImage.getHeight() * scale - spaceHight), 10);

			contentStream.close();
			doc.save(filePrefix + "results.pdf");

			doc.close();
		}

		catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	public static void imagesThirdLine(ImagePlus impOut, ImagePlus mCherry, ImagePlus fluo, String filePrefix) {

		int counter = 0;// first image

		ImageStatistics stats = mCherry.getStatistics();
		mCherry.setDisplayRange(stats.min, stats.max);
		mCherry.setDefault16bitRange(8);
		;

		IJ.run(mCherry, "Set Scale...", "distance=1.5528 known=1 pixel=1 unit=µm"); // add
																					// scale
																					// bar
		IJ.run(mCherry, "Scale Bar...",
				"width=50 height=4 font=14 " + "color=White background=None location=[Lower Right] bold");

		IJ.saveAs(mCherry, "PNG", filePrefix + "mCherry");// save image
		CreatePdf.createThirdLine(filePrefix, "mCherry", 3, counter, "Transfection control (mCherry)"); // create
																										// image
																										// in
																										// pdf

		RoiManager rm = RoiManager.getInstance(); // instantiate RoiManager
		if (rm == null)
			rm = new RoiManager();
		rm.runCommand("reset"); // reset roi manager
		Roi roi;
		ThresholdingAndRoi.createRoi(impOut); // get roi
		int nRois = rm.getCount(); // get number of rois

		ImageProcessor ip = mCherry.getProcessor();
		mCherry.setProcessor(null, ip.convertToRGB()); // convert to RGB
		ip = mCherry.getProcessor(); // new processor!
		mCherry.setCalibration(mCherry.getCalibration()); // update calibration

		for (int i = 0; i < nRois; i++) // draw the specific roi outlines
		{
			roi = rm.getRoi(i);
			roi.setStrokeColor(Color.cyan);
			roi.setStrokeWidth(1);
			ip.drawRoi(roi);
		}

		IJ.saveAs(mCherry, "PNG", filePrefix + "mCherryRoi"); // save image

		counter = 1;
		CreatePdf.createThirdLine(filePrefix, "mCherryRoi", 3, counter, "Roi by thresholder: Phansalkar");// create
																											// image
																											// in
																											// pdf

		fluo.setSlice(1); // set specific slice
		Duplicator dp = new Duplicator();
		ImagePlus fluo1 = dp.run(fluo); // set specific image from stack

		IJ.run(fluo1, "Set Scale...", "distance=1.5528 known=1 pixel=1 unit=µm"); // set
																					// scale
		IJ.run(fluo1, "Scale Bar...",
				"width=50 height=4 font=14 " + "color=White background=None location=[Lower Right] bold");

		ip = fluo1.getProcessor();
		fluo1.setProcessor(null, ip.convertToRGB()); // convert to RGB
		ip = fluo1.getProcessor();
		fluo1.setCalibration(mCherry.getCalibration()); // update calibration

		for (int i = 0; i < nRois; i++)// draw the specific roi outlines
		{
			roi = rm.getRoi(i);
			roi.setStrokeColor(Color.cyan);
			roi.setStrokeWidth(1);
			ip.drawRoi(roi);
		}

		IJ.saveAs(fluo1, "PNG", filePrefix + "fluoRoi"); // save image
		counter = 2;
		CreatePdf.createThirdLine(filePrefix, "fluoRoi", 3, counter, "Same Roi on fluo4"); // create
																							// image
																							// in
																							// pdf

	}

	public static void createThirdLine(String filePrefix, String name, Integer numberOfImages, Integer counter,
			String text) {
		int space = 5;
		int spaceHight = 10;

		try {
			PDDocument doc = null;
			doc = PDDocument.load(new File(filePrefix + "results.pdf"));
			PDPage page = doc.getPage(0);
			PDRectangle pageSize = page.getMediaBox();
			float width = pageSize.getWidth() - pageSize.getWidth() / 10;
			float x = pageSize.getWidth() / 60;

			// create object pdImage
			PDImageXObject pdImage = PDImageXObject.createFromFile(filePrefix + name + ".png", doc);
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);

			float scale = (width) / (numberOfImages * pdImage.getWidth());
			float y = (pageSize.getHeight() - (pageSize.getHeight() / 10) - 3 * pdImage.getHeight() * scale
					- 2 * (space + spaceHight));

			contentStream.drawImage(pdImage, x + (pdImage.getWidth() * scale) * counter + counter * space, y,
					pdImage.getWidth() * scale, pdImage.getHeight() * scale);

			writeTitle(contentStream, text, (int) (x + (pdImage.getWidth() * scale) * counter + counter * space),
					(int) (y) - spaceHight, 10);

			contentStream.close();
			doc.save(filePrefix + "results.pdf");
			doc.close();

		}

		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
