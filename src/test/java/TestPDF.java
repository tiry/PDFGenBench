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
import org.nuxeo.bench.gen.ITextGenerator;
import org.nuxeo.bench.gen.ITextTemplateBasedGenerator;
import org.nuxeo.bench.gen.ITextTemplateBasedGeneratorWithIdx;
import org.nuxeo.bench.gen.PDFBoxGenerator;
import org.nuxeo.bench.gen.PDFBoxUpdater;
import org.nuxeo.bench.gen.PDFGenerator;

public class TestPDF {

	
	protected static final int NB_CALLS = 2500;
	protected static final int NB_THREADS = 10;

	public int runTest(PDFGenerator gen, File in) throws Exception {		
						
		gen.init(in);
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		executor.prestartAllCoreThreads();
		AtomicInteger counter = new AtomicInteger();
		AtomicInteger genSize =  new AtomicInteger();

		
		System.out.println("----------------------------------------------------------");
		System.out.println("Testing " + gen.getName());
		System.out.println("  input file:" +  in.getName());

		
		long t0 = System.currentTimeMillis();
				
		final class Task implements Runnable {

			@Override
			public void run() {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream(60000);	
				ByteArrayOutputStream thumb = new ByteArrayOutputStream(50000);	
				for (int i = 0; i < NB_CALLS; i++) {
					try {
						buffer.reset();
						thumb.reset();
						gen.generate(buffer, thumb);
						genSize.compareAndSet(0, buffer.size() + thumb.size());
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


	@Test
	public void test() throws Exception {				
		PDFGenerator gen = new ITextTemplateBasedGeneratorWithIdx();
		URL url = this.getClass().getResource("statement_sample1.PDF");
		File in = new File(url.toURI());
		
		gen.init(in);		
		
		File out = File.createTempFile("pdf-gen", ".pdf");
		FileOutputStream stream = new FileOutputStream(out);

		File thumb = File.createTempFile("pdf-gen", ".jpg");
		FileOutputStream tmb = new FileOutputStream(thumb);

		gen.generate(stream, tmb);
		
		System.out.println(out.getAbsolutePath());
		System.out.println(thumb.getAbsolutePath());
		
		
	}

	@Test
	public void bench1() throws Exception {				
		URL url = this.getClass().getResource("statement_sample1.PDF");
		File in = new File(url.toURI());
		PDFGenerator gen = new ITextTemplateBasedGenerator();
		runTest(gen, in);		
	}


	@Test
	public void bench2() throws Exception {
		URL url = this.getClass().getResource("statement_sample1.PDF");
		File in = new File(url.toURI());
		PDFGenerator gen = new ITextTemplateBasedGeneratorWithIdx();
		runTest(gen, in);		
	}

	@Test
	public void bench3() throws Exception {
		URL url = this.getClass().getResource("bank-logo.png");
		File in = new File(url.toURI());
		PDFGenerator gen = new ITextGenerator();
		runTest(gen, in);		
	}
	
	@Test
	public void bench4() throws Exception {
		URL url = this.getClass().getResource("nx-statement-small.pdf");
		File in = new File(url.toURI());
		PDFGenerator gen = new ITextTemplateBasedGeneratorWithIdx();
		runTest(gen, in);		
	}

	
	@Test
	public void bench5() throws Exception {
		URL url = this.getClass().getResource("bank-logo.png");
		File in = new File(url.toURI());
		PDFGenerator gen = new PDFBoxGenerator();
		runTest(gen, in);		
	}

	
	@Test
	public void bench6() throws Exception {
		URL url = this.getClass().getResource("statement_sample1.PDF");
		File in = new File(url.toURI());
		PDFGenerator gen = new PDFBoxUpdater();
		runTest(gen, in);		
	}

	@Test
	public void bench7() throws Exception {
		URL url = this.getClass().getResource("statement_sample1.PDF");
		File in = new File(url.toURI());
		PDFBoxUpdater gen = new PDFBoxUpdater();
		gen.enableThumbnail=true;
		runTest(gen, in);		
	}

}
