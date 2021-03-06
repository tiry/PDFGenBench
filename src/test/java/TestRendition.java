import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.nuxeo.bench.gen.PDFGenerator;
import org.nuxeo.bench.gen.experiment.ITextTemplateBasedGeneratorWithIdx;
import org.nuxeo.bench.render.ITextPDFRenderer;
import org.nuxeo.bench.render.JavaPDFRenderer;
import org.nuxeo.bench.render.PDFBoxRenderer;
import org.nuxeo.bench.render.PDFThumbnailRenderer;
import org.nuxeo.bench.render.SpirePDFRenderer;

public class TestRendition {

	
	protected static final int NB_CALLS = 500;
	protected static final int NB_THREADS = 10;

	public int runTest(PDFThumbnailRenderer renderer, byte[] pdf) throws Exception {		
								
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		executor.prestartAllCoreThreads();
		AtomicInteger counter = new AtomicInteger();
		AtomicInteger genSize =  new AtomicInteger();

		
		System.out.println("----------------------------------------------------------");
		System.out.println("Testing " + renderer.getName());

		
		long t0 = System.currentTimeMillis();
				
		final class Task implements Runnable {

			@Override
			public void run() {
				ByteArrayOutputStream thumb = new ByteArrayOutputStream(50000);	
				for (int i = 0; i < NB_CALLS; i++) {
					try {
						thumb.reset();
						renderer.render(pdf, thumb);
						genSize.compareAndSet(0,thumb.size());
						counter.incrementAndGet();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}				
			}			
		}
				
		for (int i = 0; i < NB_THREADS; i++) {
			executor.execute(new Task());
		}		
			
		executor.shutdown();
		boolean finished = executor.awaitTermination(3*60, TimeUnit.SECONDS);
		if (!finished) {
			System.out.println("Timeout after " + counter.get() + " generations");
		}
		
		long t1 = System.currentTimeMillis();
		
		Double throughput = counter.get() * 1.0 /((t1-t0)/1000);		
		

		
		System.out.println("  Files: " + counter.get() + " pdfs --- " + throughput.intValue() + " docs/s");
		System.out.println("  IO   : " + (counter.get()*genSize.intValue())/(1024*1024) + " MB  --- " + (throughput.intValue()*genSize.intValue())/(1024*1024) + " MB/s");

		System.out.print("\n  Projected generation time for 10B files: ");
		Duration d = Duration.ofSeconds(10000000000L/throughput.intValue());
		System.out.println(d.toDaysPart() + " day(s) and " + d.toHoursPart() + " hour(s)");		
		
		return throughput.intValue();
	}

	protected byte[] getSourcePDF() throws Exception {

		PDFGenerator gen = new ITextTemplateBasedGeneratorWithIdx();
		URL url = this.getClass().getResource("statement_sample1.PDF");
		File in = new File(url.toURI());
		gen.init(in);		

		ByteArrayOutputStream buffer = new ByteArrayOutputStream(60000);	

		gen.generate(buffer, null);
		
		return buffer.toByteArray(); 
	}

	@Test
	public void bench1() throws Exception {				
		byte[] pdf = getSourcePDF();
		PDFBoxRenderer renderer = new PDFBoxRenderer();		
		runTest(renderer, pdf);		
	}
	
	@Test
	public void bench2() throws Exception {				
		byte[] pdf = getSourcePDF();
		JavaPDFRenderer renderer = new JavaPDFRenderer();		
		runTest(renderer, pdf);		
	}

	@Test
	public void bench3() throws Exception {				
		byte[] pdf = getSourcePDF();
		SpirePDFRenderer renderer = new SpirePDFRenderer();		
		runTest(renderer, pdf);		
	}
	
	
}
