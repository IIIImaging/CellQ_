package CreatePDF;

public class MainCreatePDF {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String imagepath = "C:/Users/rassmann/Desktop/test/newmap.jpg";
		String filePrefix = "C:/Users/rassmann/Desktop/test/";

		CreatePdf.generateDocument(filePrefix + "results.pdf");
		// CreatePdf.insertImage(filePrefix+ "results.pdf", imagepath, x, y);

		CreatePdf.insertImage(filePrefix + "results.pdf", imagepath, 1000, 10000);

	}

}
