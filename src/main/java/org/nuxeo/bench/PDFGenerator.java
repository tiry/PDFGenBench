package org.nuxeo.bench;

import java.io.File;
import java.io.OutputStream;

public interface PDFGenerator {

	
	String getName();
	
	void init(File input) throws Exception;
	
	void generate(OutputStream pdf, OutputStream thumb) throws Exception;
	
}
