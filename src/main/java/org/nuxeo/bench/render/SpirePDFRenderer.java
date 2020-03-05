package org.nuxeo.bench.render;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.spire.pdf.PdfDocument;

public class SpirePDFRenderer implements PDFThumbnailRenderer {


	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void render(byte[] pdf, OutputStream thumb) throws Exception {

        PdfDocument doc = new PdfDocument();
        doc.loadFromStream(new ByteArrayInputStream(pdf));
        BufferedImage bufferedImage = doc.saveAsImage(0);		
		ImageIO.write(bufferedImage, "png", thumb);		
	}

}
