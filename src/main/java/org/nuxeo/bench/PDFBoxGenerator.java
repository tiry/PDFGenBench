package org.nuxeo.bench;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class PDFBoxGenerator implements PDFGenerator {

	@Override
	public String getName() {
		return "Generate a new PDF Document using PDFBox";
	}
	
	protected byte[] imgData;
	protected PDFont font;
	
	@Override
	public void init(File input) throws Exception {

		imgData = Files.readAllBytes(input.toPath());
		font = PDType1Font.HELVETICA_BOLD;
        
	}

	@Override
	public void generate(OutputStream out, OutputStream thumb) throws Exception {
		PDDocument doc = new PDDocument();
        PDPage page = new PDPage();

        PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, imgData, "");

        doc.addPage(page);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, true, true))
        {
                // contentStream.drawImage(ximage, 20, 20 );
                // better method inspired by http://stackoverflow.com/a/22318681/535646
                // reduce this value if the image is too large
                float scale = 1f;
                contentStream.drawImage(pdImage, 20, 20, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
                
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Bank Statement ");
                contentStream.endText();
        }                
        doc.save(out);
        
        doc.close();
	}

}
