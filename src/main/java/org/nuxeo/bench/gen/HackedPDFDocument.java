package org.nuxeo.bench.gen;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

public class HackedPDFDocument extends PdfDocument {

	public HackedPDFDocument(PdfReader reader, PdfWriter writer) {
		super(reader, writer);
	}
	
	protected void updateXmpMetadata() {
		// NOP
	}

}
