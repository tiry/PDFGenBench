package org.nuxeo.bench;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

public class ITextTemplateBasedGeneratorWithIdx extends ITextTemplateBasedGenerator {

	protected Map<Integer, byte[]> index = new HashMap<Integer, byte[]>();
	
	public String getName() {		
		return "Template based generation with Index pre-processing using iText";
	}

	public void init(File pdf) throws Exception {	
		
		super.init(pdf);		
		
	    
		PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(template));					
		PdfDocument doc = new PdfDocument(pdfReader);

		PdfPage page = doc.getFirstPage();
        PdfDictionary dict = page.getPdfObject();

        PdfObject object = dict.get(PdfName.Contents);

        if (object instanceof PdfStream) {
            PdfStream stream = (PdfStream) object;
            byte[] data = stream.getBytes();
            String txt = new String(data);
		    for (String key: replacements.keySet()) {		    	
		    	int idx = 0;
		    	do {
		    		idx = txt.indexOf(key, idx);
		    		if (idx > 0) {
		    			index.put(idx,  replacements.get(key).getBytes());
		    		}
		    		idx++;
		    	} while (idx > 0);	    	
		    }
        }
        
        doc.close();
	    						
	}
	
	public void generate(OutputStream buffer, OutputStream thumb) throws Exception {

		PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(template));					
		PdfWriter writer = new PdfWriter(buffer);		
		PdfDocument doc = new PdfDocument(pdfReader, writer);
		
		PdfPage page = doc.getFirstPage();
        PdfDictionary dict = page.getPdfObject();

        PdfObject object = dict.get(PdfName.Contents);

        if (object instanceof PdfStream) {
            PdfStream stream = (PdfStream) object;
            byte[] data = stream.getBytes();
         
            for (Integer idx: index.keySet()) {
            	byte[] chunk = index.get(idx);
            	for (int i = 0; i < chunk.length; i++ ) {
            		data[idx+i] = chunk[i]; 
            	}
            }            
            stream.setData(data);
        }		
        
        PdfStream thumbnailStream = doc.getFirstPage().getPdfObject().getAsStream(PdfName.Thumb);
        if (thumbnailStream != null) {
            PdfImageXObject thumbnail = new PdfImageXObject(thumbnailStream);
            BufferedImage image = thumbnail.getBufferedImage();
            //Output to file, memory, etc
        }
        
        doc.close();
		
	}
	
}
