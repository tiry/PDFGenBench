package org.nuxeo.bench.render;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

import com.itextpdf.kernel.utils.PageRange;
import com.itextpdf.pdfrender.PdfRenderImageType;
import com.itextpdf.pdfrender.PdfToImageRenderer;
import com.itextpdf.pdfrender.RenderingProperties;

public class ITextPDFRenderer implements PDFThumbnailRenderer {

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void render(byte[] pdf, OutputStream thumb) throws Exception {

		RenderingProperties rp = new RenderingProperties();
		rp.setImageType(PdfRenderImageType.PNG);
		rp.setPageScaling(0.3f);
		
		PageRange pr = new PageRange();
		pr.addSinglePage(0);
		
		File file = java.io.File.createTempFile("thumb", ".png");		
	    PdfToImageRenderer.renderPdf(new ByteArrayInputStream(pdf),file, rp);
	    Files.copy(file.toPath(), thumb);		
	}

}
