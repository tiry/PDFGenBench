package org.nuxeo.bench.gen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.bench.rnd.RandomDataGenerator;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;

public class ITextNXBankStatementGenerator implements PDFGenerator {

	protected File pdf;
	
	protected byte[] template;

	protected Map<Integer, Integer> index = new HashMap<Integer, Integer>();
	
	
	protected String[] keys;
	
	protected RandomDataGenerator rndGen;
	
	public String getName() {		
		return "Template based generation with Index pre-processing using iText";
	}

	public void setRndGenerator(RandomDataGenerator rndGen) {
		this.rndGen = rndGen;
	}
	
	public void init(File pdf) throws Exception {	
		
		this.pdf = pdf;		
		template = Files.readAllBytes(pdf.toPath());

		keys = new String[] { 
				"#NAME-----------------------------------#",
				"#STREET------------#",
				"#CITY--------------#", 
				"#STATE-------------#",				
				"#DATE--------------#",
				"#ACCOUNTID---------#",};
		
		PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(template));					
		PdfDocument doc = new PdfDocument(pdfReader);

		PdfPage page = doc.getFirstPage();
        PdfDictionary dict = page.getPdfObject();
                
        PdfObject object = dict.get(PdfName.Contents);

        if (object instanceof PdfStream) {
            PdfStream stream = (PdfStream) object;
            byte[] data = stream.getBytes();
            String txt = new String(data);
            for (int k = 0; k < keys.length; k++ ) {
            	String key = keys[k];
		    	int idx = 0;
		    	do {
		    		idx = txt.indexOf(key, idx);
		    		if (idx > 0) {
		    			index.put(idx, k);
		    		}
		    		idx++;
		    	} while (idx > 0);	    	            	
            }
        }        
        doc.close();	    					
	}
	
	public void generate(OutputStream buffer, OutputStream thumb) throws Exception {

		String[] tokens = rndGen.generate();		
		PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(template));	
				
		WriterProperties wp = new WriterProperties();
		wp.setPdfVersion(PdfVersion.PDF_1_4);
		wp.useSmartMode();
		
		PdfWriter writer = new PdfWriter(buffer, wp);		
		PdfDocument doc = new HackedPDFDocument(pdfReader, writer);

		PdfPage page = doc.getFirstPage();
        PdfDictionary dict = page.getPdfObject();
        PdfObject object = dict.get(PdfName.Contents);

        if (object instanceof PdfStream) {
            PdfStream stream = (PdfStream) object;
            byte[] data = stream.getBytes();
         
            for (Integer idx: index.keySet()) {
            	byte[] chunk = tokens[index.get(idx)].getBytes();
            	System.arraycopy(chunk, 0, data, idx, chunk.length);
            }            
            stream.setData(data);
        }		                
        doc.close();		
	}
	
}
