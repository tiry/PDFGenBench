import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.nuxeo.bench.gen.ITextNXBankStatementGenerator;
import org.nuxeo.bench.gen.smt.Injector;
import org.nuxeo.bench.rnd.RandomDataGenerator;

public class TestInjector {

	
	@Test
	public void testInjector() throws Exception {
		
		RandomDataGenerator rnd = new RandomDataGenerator();
		URL csvurl = this.getClass().getResource("data.csv");
		File csv = new File(csvurl.toURI());
		rnd.init(csv);
		
		ITextNXBankStatementGenerator gen = new ITextNXBankStatementGenerator();
		URL url = this.getClass().getResource("nxbank-template.pdf");
		File in = new File(url.toURI());
		gen.init(in);
		gen.computeDigest=true;
		gen.setRndGenerator(rnd);		
		
		Injector injector = new Injector(gen, 1000000);
		
		injector.run();

		
	}
}
