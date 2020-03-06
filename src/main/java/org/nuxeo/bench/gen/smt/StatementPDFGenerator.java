package org.nuxeo.bench.gen.smt;

import java.io.File;
import java.io.OutputStream;

public interface StatementPDFGenerator {

	void init(File pdfTemplate) throws Exception;

	SmtMeta generate(OutputStream pdf) throws Exception;

}
