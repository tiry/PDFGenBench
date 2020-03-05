package org.nuxeo.bench.render;

import java.io.OutputStream;

public interface PDFThumbnailRenderer {
	
	String getName();

	void render(byte[] pdf, OutputStream thumb) throws Exception;

	
}
