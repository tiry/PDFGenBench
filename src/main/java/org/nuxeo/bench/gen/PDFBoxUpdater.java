package org.nuxeo.bench.gen;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFBoxUpdater implements PDFGenerator {

	@Override
	public String getName() {
		String name =  "Update an existing PDF template using PDFBox";
		if (enableThumbnail) {
			name += " and generate Thumbnails";
		}
		return name;
	}
	
	protected byte[] pdf;
	
	protected Map<String, String> replacements = new HashMap<String, String>();

	public boolean enableThumbnail = false;
	
	@Override
	public void init(File input) throws Exception {

		pdf = Files.readAllBytes(input.toPath());

		replacements.put("Jane Customer", "Jacky Chan");
		replacements.put("000009752", "123456789");
		replacements.put("2003", "2020");
		replacements.put("20.00", "41");
		replacements.put("10,521.19", "10,500.19");

	}

	@Override
	public void generate(OutputStream out, OutputStream thumb) throws Exception {
		
		 	       
		PDDocument doc = PDDocument.load(pdf);
		
        PDPage page = doc.getPage(0);
        
        PDStream content =  page.getContentStreams().next();
        
        byte[] data = content.toByteArray();

        String replacedData = new String(data);
        
        for (String key: replacements.keySet()) {
        	replacedData = replacedData.replace(key, replacements.get(key));	
        }
        
        PDStream newStream = new PDStream(doc, new ByteArrayInputStream(replacedData.getBytes()));        
        page.setContents(newStream);
       
                
        doc.save(out);

        if (thumb!=null && enableThumbnail) {
	        PDFRenderer pdfRenderer = new PDFRenderer(doc);
	        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 36, ImageType.GRAY);        
	        ImageIO.write(bim,"png", thumb);
        }
	        
        doc.close();
	}

}
