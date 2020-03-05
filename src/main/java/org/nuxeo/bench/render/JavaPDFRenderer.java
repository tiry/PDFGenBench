package org.nuxeo.bench.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class JavaPDFRenderer implements PDFThumbnailRenderer {

	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void render(byte[] pdf, OutputStream thumb) throws Exception {
		
		ByteBuffer buf = ByteBuffer.wrap(pdf);
        PDFFile pdfFile = new PDFFile(buf);
        
        PDFPage page = pdfFile.getPage(0);
        
        Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(),
                (int) page.getBBox().getHeight());
        BufferedImage bufferedImage = new BufferedImage(rect.width, rect.height,
                         BufferedImage.TYPE_INT_RGB);

        Image image = page.getImage(rect.width, rect.height,    // width & height
                   rect,                       // clip rect
                   null,                       // null for the ImageObserver
                   true,                       // fill background with white
                   true                        // block until drawing is done
        );
        Graphics2D bufImageGraphics = bufferedImage.createGraphics();
        bufImageGraphics.drawImage(image, 0, 0, null);
        ImageIO.write(bufferedImage, "png", thumb);		
		
	}

}
