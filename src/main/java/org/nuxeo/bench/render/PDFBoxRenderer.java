package org.nuxeo.bench.render;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFBoxRenderer implements PDFThumbnailRenderer {

	
	public void render(byte[] pdf, OutputStream thumb) throws Exception {
		
		PDDocument doc = PDDocument.load(pdf);
        PDFRenderer pdfRenderer = new PDFRenderer(doc);
        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 36, ImageType.GRAY);        
        ImageIO.write(bim,"png", thumb);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	
}
