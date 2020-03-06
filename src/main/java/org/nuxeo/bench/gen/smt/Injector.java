package org.nuxeo.bench.gen.smt;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Injector {

	protected static final int NB_THREADS = 10;
	protected static final int BUFFER_SIZE = 10*2014;;

	protected int total;	
	protected  int callsPerThreads = 5000;	
	protected final StatementPDFGenerator gen;
	
	public Injector(StatementPDFGenerator gen, int total) {
		this.gen=gen;
		this.total=total;
		this.callsPerThreads = Math.round(total/NB_THREADS) + 1;
	}
	
	public int run() throws Exception {		
										
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		executor.prestartAllCoreThreads();
		AtomicInteger counter = new AtomicInteger();
		AtomicInteger genSize =  new AtomicInteger();
		
		System.out.println("----------------------------------------------------------");
		
		long t0 = System.currentTimeMillis();
				
		final class Task implements Runnable {

			@Override
			public void run() {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream(BUFFER_SIZE);	
				for (int i = 0; i < callsPerThreads; i++) {
					try {
						buffer.reset();
						SmtMeta meta = gen.generate(buffer);						
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
		boolean finished = false;
		Long throughput;
		while(!finished) {			
		
			long t1 = System.currentTimeMillis();	
			long count = counter.get();
			int threads = executor.getActiveCount();

			throughput = Math.round(counter.get() * 1.0 /((t1-t0)/1000));					
			System.out.println(count + "/" + total + " (" + throughput + " d/s using " + threads + " threads)");
			
			finished = executor.awaitTermination(20, TimeUnit.SECONDS);
		}
		
		long t1 = System.currentTimeMillis();		
		throughput = Math.round(counter.get() * 1.0 /((t1-t0)/1000));		
		
		System.out.println("  Files: " + counter.get() + " pdfs --- " + throughput.intValue() + " docs/s");
		//System.out.println("  IO   : " + (counter.get()*genSize.intValue())/(1024*1024) + " MB  --- " + (throughput.intValue()*genSize.intValue())/(1024*1024) + " MB/s");

		System.out.print("\n  Projected generation time for 10B files: ");
		Duration d = Duration.ofSeconds(10000000000L/throughput.intValue());
		System.out.println(d.toDaysPart() + " day(s) and " + d.toHoursPart() + " hour(s)");		
		
		return throughput.intValue();
	}

}
