package org.nuxeo.bench;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

public class ITextGenerator implements PDFGenerator {

	@Override
	public String getName() {
		return "Generate a new PDF document using iText";
	}

	ImageData img;
	
	@Override
	public void init(File image) throws Exception {

		byte[] data = Files.readAllBytes(image.toPath());
		img = ImageDataFactory.create(data);		
		
	}

	@Override
	public void generate(OutputStream out, OutputStream thumb) throws Exception {

		PdfDocument pdfDocument = new PdfDocument(new PdfWriter(out));
		pdfDocument.setDefaultPageSize(PageSize.LETTER);
		Document document = new Document(pdfDocument);
		document.add(
				new Paragraph()
				.add(new Image(img)));				
		document.add(
				new Paragraph()
				.setFontSize(20)
				.add(new Text("Bank Statement ")));
		document.add(
				new Paragraph()
				.setFontSize(14)		
				.add(new Text("Account # 123456789\n"))
				.add(new Text("Beginning Balance on May 3, 2019")));
		document.close();
				
	}

}
