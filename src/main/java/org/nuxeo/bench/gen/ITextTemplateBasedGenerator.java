package org.nuxeo.bench.gen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
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

public class ITextTemplateBasedGenerator implements PDFGenerator{

	protected File pdf;
	
	protected byte[] template;
	
	protected Map<String, String> replacements = new HashMap<String, String>();
	
	
	public String getName() {		
		return "Template based generation using iText";
	}
	
	public void init(File pdf) throws Exception {
		this.pdf = pdf;		
		template = Files.readAllBytes(pdf.toPath());
		
		replacements.put("Jane Customer", "Jacky Bitou");
		replacements.put("000009752", "123456789");
		replacements.put("2003", "2019");
		replacements.put("20.00", "40");
		replacements.put("10,521.19", "10,501.19");
				
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
            String replacedData = new String(data);
            
            for (String key: replacements.keySet()) {
            	replacedData = replacedData.replace(key, replacements.get(key));	
            }
            stream.setData(replacedData.getBytes(StandardCharsets.UTF_8));
        }
		
        doc.close();
		
	}
	
}
