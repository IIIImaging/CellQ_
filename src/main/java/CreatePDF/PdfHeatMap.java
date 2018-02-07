package CreatePDF;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import ij.IJ;

public class PdfHeatMap {

	/**
	 * @param pdfpath
	 *            : path of the pdf-Document where the Page with the mapper
	 *            should be added
	 * @param imagepath
	 *            : path of the mapper to be added to the document
	 * @param targetpath
	 *            : path to save the modified document, if it is identical to
	 *            the raw Pdf path, the raw Pdf is overwritten
	 * @param imageframe
	 *            : the Position and size of the mapper on the new pdf page as
	 *            start x, start y, width, height (f.e. (70,330,500,350))
	 * @param starttime
	 *            : the time of the first measurement on the mapper in sec
	 * @param endtime
	 *            : the time of the last measurement on the mapper in sec
	 * @param flaspostion
	 *            : the TIME of the flash in sec
	 * @param numberofwrittentimepoints
	 *            : the numbers of timepoints that will be shown below the
	 *            mapper
	 * @param intensities
	 *            : the average transfection control intensity of each cell
	 *            (index has to match with the index used on the mapper)
	 * @param headline
	 *            : Text will be written above the mapper
	 * 
	 * @return the index of the page where the mapper and the description were
	 *         added
	 * 
	 **/

	public static int insertDecriptedImage(final String pdfpath, final String imagepath, final String targetpath,
			final float starttime, final float endtime, final int flashposition, final byte numberofwrittentimepoints,
			final double[] intensities, final String headline, final String subtitle)
					throws IOException, Throwable {

		int numberofintensities = intensities.length;
		int ymode = (numberofintensities / 50) + 1;

		int heightofline = 12;
		int image_heigth = numberofintensities / ymode * heightofline;
		int image_start_y = 685 - image_heigth;
		Rectangle imageframe = new Rectangle(70, image_start_y, 500, image_heigth); // start
																					// x,
																					// start
																					// y,
																					// width,
																					// height
																					// of
																					// the
																					// map
																					// in
																					// the
																					// pdf
		File file = new File(pdfpath);
		PDDocument doc = PDDocument.load(file);
		int pagewithmapper = doc.getNumberOfPages();
		doc.addPage(new PDPage());
		PDPage page = doc.getPage(pagewithmapper);
		try {
			PDImageXObject pic = PDImageXObject.createFromFile(imagepath, doc);
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			contentStream.drawImage(pic, imageframe.x, imageframe.y, imageframe.width, imageframe.height);

			contentStream = insertTextField(contentStream, 60, 750, 20, headline, PDType1Font.HELVETICA_BOLD); // add
																												// headline
			contentStream = insertTextField(contentStream, 60, 725, 10, subtitle, PDType1Font.HELVETICA_BOLD);

			float xdistance = imageframe.width / (numberofwrittentimepoints - 1); // represents
																					// the
																					// distance
																					// between
																					// two
																					// shown
																					// time
																					// informations
																					// below
																					// the
																					// mapper
			float trange = (endtime - starttime);
			float tdistanceoftime = trange / (numberofwrittentimepoints - 1); // represents
																				// the
																				// time
																				// difference
																				// between
																				// the
																				// shown
																				// descriptions
																				// (for
																				// example
																				// 10s
			for (int i = 0; i < numberofwrittentimepoints; i++) {
				contentStream = insertTextField(contentStream, imageframe.x + (i * xdistance) - 5, imageframe.y - 40,
						10, starttime + (i * tdistanceoftime));
			}

			float sizeofroidescription = 6f; // fontsize of the Roi-description
												// to the right
			float centratefactor_y = (heightofline - sizeofroidescription) / 2; 
			float ydistance = (imageframe.height) / (numberofintensities);
			for (int i = 0; i < numberofintensities; i++) {
				if (i % ymode == 0) {
					float roundedvalue = Math.round(intensities[i] * 10);
					roundedvalue = roundedvalue / 10;
					int numberofdigits = (int) Math.log10(roundedvalue);
					contentStream = insertTextField(contentStream, imageframe.x - 15 - 3 * numberofdigits,
							imageframe.y + (ydistance * i) + centratefactor_y + 0.7f, sizeofroidescription,
							roundedvalue);
					contentStream.drawLine(imageframe.x - 20, imageframe.y + (ydistance * i), imageframe.x,
							imageframe.y + (ydistance * i));
				}
			}
			contentStream.drawLine(imageframe.x - 20, imageframe.y + imageframe.height, imageframe.x,
					imageframe.y + imageframe.height);

			// PDImageXObject arrow =
			// PDImageXObject.createFromFile(getArrowPath(pdfpath), doc);
			// //arrow png has to in the same folder as raw pdf an has to be
			// named "arrow.png"
			// contentStream.drawImage(arrow, imageframe.x, imageframe.y-25,
			// imageframe.width, 20);

			contentStream = insertTextField(contentStream, imageframe.x + (imageframe.width / 2) - 30,
					imageframe.y - 70, 12, "Time (s)");
			contentStream = insertTextField(contentStream, imageframe.x - 45, imageframe.y + imageframe.height + 20, 12,
					"Roi Intensity");

			if (flashposition > 10) {
				contentStream.setNonStrokingColor(Color.RED);
				contentStream = insertTextField(contentStream,
						imageframe.x + (imageframe.width * (flashposition / trange)), imageframe.y - 12, 15, "F",
						PDType1Font.HELVETICA_BOLD);
			}

			contentStream = drawArrow(contentStream, imageframe.x, imageframe.y - 15, imageframe.x + imageframe.width,
					imageframe.y - 15, 32, 26);
			contentStream.close();
			doc.save(targetpath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc.close();
		
		
		
		return pagewithmapper;
	}

	@SuppressWarnings("deprecation")
	public static PDPageContentStream drawHorizontalArrow(PDPageContentStream contentStream, float xstart, float xend,
			float y, float hight, float width, float thickness) throws IOException {

		// http://www.verypdf.com/document/pdf-format-reference/pg_0218.htm to
		// see dash pattern

		float e1x = xend - width;
		float e2x = xend - width;
		float e1y = y - hight;
		float e2y = y + hight;

		contentStream.drawLine(xstart, y, xend, y);
		contentStream.drawLine(xend, y, e1x, e1y);
		contentStream.drawLine(xend, y, e2x, e2y);

		return contentStream;
	}

	public static PDPageContentStream drawLine(PDPageContentStream contentStream, float xstart, float xend, float y,
			float thickness) throws IOException {

		// http://www.verypdf.com/document/pdf-format-reference/pg_0218.htm to
		// see dash pattern

		contentStream.drawLine(xstart, y, xend, y);

		return contentStream;
	}

	@SuppressWarnings("deprecation")
	public static PDPageContentStream drawArrow(PDPageContentStream contentStream, float xstart, float ystart,
			float xend, float yend, float arrowheadlenght, double angledegree) throws IOException {

		// http://www.verypdf.com/document/pdf-format-reference/pg_0218.htm to
		// see dash pattern

		double anglerad = Math.toRadians(angledegree);
		double angleofmainline = Math.atan((yend - ystart) / Math.abs(xend - xstart));
		double angletoxaxis1 = angleofmainline - anglerad;
		double angletoxaxis2 = angleofmainline + anglerad;
		double angletoyaxis1 = angleofmainline - anglerad - Math.PI * 0.5;
		double angletoyaxis2 = angleofmainline + anglerad - Math.PI * 0.5;

		float e1x = (float) (xend - Math.cos(angletoxaxis1) * arrowheadlenght);
		float e2x = (float) (xend - Math.cos(angletoxaxis2) * arrowheadlenght);
		float e1y = (float) (yend - Math.cos(angletoyaxis1) * arrowheadlenght);
		float e2y = (float) (yend - Math.cos(angletoyaxis2) * arrowheadlenght);

		contentStream.drawLine(xstart, ystart, xend, yend);
		contentStream.drawLine(xend, yend, e1x, e1y);
		contentStream.drawLine(xend, yend, e2x, e2y);

		return contentStream;
	}

	public static PDPageContentStream drawArrow_Test(PDPageContentStream contentStream, float xstart, float ystart,
			float xend, float yend, float arrowheadlenght, double angledegree) throws IOException {

		// http://www.verypdf.com/document/pdf-format-reference/pg_0218.htm to
		// see dash pattern

		double anglerad = Math.toRadians(angledegree);
		double deltax = xend - xstart;
		double deltay = yend - ystart;
		double angleofmainline;
		// if(deltax >= 0){
		angleofmainline = Math.atan(deltay / deltax);
		// }
		// else{
		// angleofmainline = Math.atan(deltay/deltax) + Math.PI*0.5;
		// }
		double angletoxaxis1 = angleofmainline - anglerad;
		double angletoxaxis2 = angleofmainline + anglerad;
		double angletoyaxis1 = angleofmainline - anglerad - Math.PI * 0.5;
		double angletoyaxis2 = angleofmainline + anglerad - Math.PI * 0.5;

		float e1x = (float) (xend - Math.cos(angletoxaxis1) * arrowheadlenght);
		float e2x = (float) (xend - Math.cos(angletoxaxis2) * arrowheadlenght);
		float e1y = (float) (yend - Math.sin(angletoyaxis1) * arrowheadlenght);
		float e2y = (float) (yend - Math.sin(angletoyaxis2) * arrowheadlenght);
		float px = (float) (xend + 15 * Math.cos(angleofmainline));
		float py = (float) (yend + 15 * Math.cos(angleofmainline));

		System.out.println(Math.toDegrees(angleofmainline));
		System.out.println(Math.toDegrees(angletoxaxis1));
		System.out.println(Math.toDegrees(angletoxaxis2));
		System.out.println(Math.toDegrees(angletoyaxis1));
		System.out.println(Math.toDegrees(angletoyaxis2));
		System.out.println("---------");

		contentStream.drawLine(xstart, ystart, xend, yend);
		contentStream.drawLine(xend, yend, e1x, e1y);
		contentStream.drawLine(xend, yend, e2x, e2y);
		contentStream.drawLine(xend, yend, e2x, e2y);
		contentStream.drawLine(xend, yend, px, py);

		return contentStream;
	}

	/**
	 * @param contentStream:
	 *            opened contentStream where the text should be added
	 * @param x:
	 *            x Coordinate of the textfield
	 * @param y:
	 *            y Coordinate of the textfield
	 * @param text:
	 *            text content to be addes as String
	 * @param textsize:
	 *            float representing the size (f.e. 10f)
	 * 
	 *            the method creates the textfield at the given coordinates,
	 *            size and Standart Times Roman font with the given text
	 * 
	 **/

	public static PDPageContentStream insertTextField(PDPageContentStream contentStream, float x, float y,
			float textsize, String text) throws IOException {
		contentStream.beginText();
		contentStream.setFont(PDType1Font.TIMES_ROMAN, textsize);
		contentStream.newLineAtOffset(x, y);
		contentStream.showText(text);
		contentStream.endText();
		return contentStream;
	}

	/**
	 * @param contentStream:
	 *            opden contentStream where the text should be added
	 * @param x:
	 *            x Coordinate of the textfield
	 * @param y:
	 *            y Coordinate of the textfield
	 * @param text:
	 *            text content to be added as float
	 * @param textsize:
	 *            float representing the size (f.e. 10f)
	 * 
	 *            the method creates the textfield at the given coordinates,
	 *            size and Standart Times Roman font with the given number
	 * @warning
	 **/

	public static PDPageContentStream insertTextField(PDPageContentStream contentStream, float x, float y,
			float textsize, float text) throws IOException {
		contentStream = insertTextField(contentStream, x, y, textsize, Float.toString(text));
		return contentStream;
	}

	public static PDPageContentStream insertTextField(PDPageContentStream contentStream, float x, float y,
			float textsize, double text) throws IOException {
		contentStream = insertTextField(contentStream, x, y, textsize, Double.toString(text));
		return contentStream;
	}

	/**
	 * @param contentStream:
	 *            contentStream where the text should be added
	 * @param x:
	 *            x Coordinate of the textfield
	 * @param y:
	 *            y Coordinate of the textfield
	 * @param text:
	 *            text content to be added as String
	 * @param textsize:
	 *            float representing the size (f.e. 10f)
	 * @param font:
	 *            font which will be used for the text
	 * 
	 *            the method creates the textfield at the given coordinates,
	 *            size and font with the given text
	 * 
	 **/

	public static PDPageContentStream insertTextField(PDPageContentStream contentStream, float x, float y,
			float textsize, String text, PDType1Font font) throws IOException {
		contentStream.beginText();
		contentStream.setFont(font, textsize);
		contentStream.newLineAtOffset(x, y);
		contentStream.showText(text);
		contentStream.endText();
		return contentStream;
	}

	/**
	 * @param pdfpath
	 *            : path of the pdf-Document where the Page with the mapper
	 *            should be added
	 * @param imagepath
	 *            : path of the mapper to be added to the document
	 * @param targetpath
	 *            : path to save the modified document, if it is identical to
	 *            the raw Pdf path, the raw Pdf is overwritten
	 * @param imageframe
	 *            : the Position and size of the mapper on the new pdf page as
	 *            start x, start y, width, height (f.e. (70,330,500,350))
	 * @param starttime
	 *            : the time of the first measurement on the mapper in sec
	 * @param endtime
	 *            : the time of the last measurement on the mapper in sec
	 * @param flaspostion
	 *            : the TIME of the flash in sec
	 * @param intensities
	 *            : the average transfection control intensity of each cell
	 *            (index has to match with the index used on the mapper)
	 * @param headline
	 *            : Text will be written above the mapper
	 * 
	 * @return the index of the page where the mapper and the description were
	 *         added
	 * 
	 **/

	public static int insertDescriptedImage(String pdfpath, String imagepath, String targetpath, int starttime,
			int endtime, int flashposition, double[] intensities, String headline) throws IOException, Throwable {
		byte numberofwrittentimepoints = 5;
		int pagewithmap = PdfHeatMap.insertDecriptedImage(pdfpath, imagepath, targetpath, starttime, endtime,
				flashposition, numberofwrittentimepoints, intensities, headline, "nofolder");
		return pagewithmap;

	}

	public static String getArrowPath(String pdfpath) {
		String arrowpath = pdfpath.substring(0, pdfpath.lastIndexOf("/")) + "/arrow.png";
		System.out.println(arrowpath);
		return arrowpath;
	}
}
