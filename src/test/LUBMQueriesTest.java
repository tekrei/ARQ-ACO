package test;

import arqaco.ai.ds.ACOParameters;
import arqaco.transform.OptimizationTransform;
import arqaco.transform.StockerTransformONS;
import arqaco.utility.OntologyOperation;
import arqaco.utility.SWANTLogger;

/**
 * Class for LUBM experiments
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class LUBMQueriesTest extends ExecuteQueries {

	private static String[] kernel = { "/bin/sh", "-c", "uname -a" };
	private static String[] cpu = { "/bin/sh", "-c",
			"cat /proc/cpuinfo | grep \"model name\" | head -n1" };
	private static String[] memory = { "/bin/sh", "-c",
			"free -m | grep \"Mem:\"" };

	private static void exec(String[] command) throws Exception {
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();

		java.io.BufferedReader reader = new java.io.BufferedReader(
				new java.io.InputStreamReader(p.getInputStream()));

		String line = "";
		while ((line = reader.readLine()) != null) {
			SWANTLogger.severen(line);
		}
	}

	private static void compConf() throws Exception {
		exec(kernel);
		exec(cpu);
		exec(memory);
	}

	public static void main(String[] args) throws Exception {
		startTest();
	}

	private static void startTest() throws Exception {
		// log computer configuration if the machine is GNU/Linux
		compConf();

		long start = System.currentTimeMillis();
		// Load RDF data and index if required
		OntologyOperation.loadOntology("data/univ-bench.owl");
		String ontology = "data/university_v2.zip";
		OntologyOperation.loadOntology(ontology);
		SWANTLogger.severen(ontology + " loaded:"
				+ (System.currentTimeMillis() - start));
		// load probability index
		OntologyOperation.prepareForPFJ("data/university_v2_index.owl");

		// how many times to run
		execCount = 10;
		ACOParameters.randomStartPointFactor = 1f;

		// log ACO parameters
		ACOParameters.printParameters();

		// single execution to initialize the classes
		OntologyOperation.executeQuery(OntologyOperation
				.loadQueryFromFile("queries/4-Cyclic.qr").query);
		OntologyOperation
				.executeQuery(OntologyOperation
						.loadQueryFromFile("queries/Query-8.qr").query,
						new StockerTransformONS());
		OntologyOperation
				.executeQuery(OntologyOperation
						.loadQueryFromFile("queries/Query-8.qr").query,
						new OptimizationTransform());

		// folder test
		folderTest();
		// single file test
		// singleTest();
		// commutativeTest();
		// timeoutQueries();
	}

	/**
	 * @override
	 */
	public static void folderTest() {
		executeFiles("queries/");
	}

	@SuppressWarnings("unused")
	private static void singleTest() {

		// SWANTLogger.severen("8-Chain.qr");
		// testQuery(OntologyOperation.loadQueryFromFile("queries/8-Chain.qr"));

		// SWANTLogger.severen("8-Cyclic.qr");
		// testQuery(OntologyOperation.loadQueryFromFile("queries/8-Cyclic.qr"));

		// SWANTLogger.severen("8-Star.qr");
		// testQuery(OntologyOperation.loadQueryFromFile("queries/8-Star.qr"));

		// SWANTLogger.severen("10-Cyclic.qr");
		// testQuery(OntologyOperation.loadQueryFromFile("queries/10-Cyclic.qr"));

		// SWANTLogger.severen("10-Chain.qr");
		// testQuery(OntologyOperation.loadQueryFromFile("queries/10-Chain.qr"));

		// SWANTLogger.severen("6-Star.qr");
		// testQuery(OntologyOperation.loadQueryFromFile("queries/6-Star.qr"));

		SWANTLogger.severen("Query-1.qr");
		testQuery(OntologyOperation
				.loadQueryFromFile("queries/notexperimented/Query-1.qr"));

		SWANTLogger.severen("Query-2.qr");
		testQuery(OntologyOperation.loadQueryFromFile("queries/Query-2.qr"));

		SWANTLogger.severen("Query-8.qr");
		testQuery(OntologyOperation.loadQueryFromFile("queries/Query-8.qr"));

		// SWANTLogger.severen("Query-9.qr");
		// testQuery(OntologyOperation.loadQueryFromFile("queries/Query-9.qr"));

		// SWANTLogger.severen("14-Chain-Star.qr");
		// testQuery(OntologyOperation.loadQueryFromFile("queries/14-Chain-Star.qr"));

	}

	@SuppressWarnings("unused")
	private static void timeoutQueries() {

		SWANTLogger.severen("4-Chain.qr");
		testQuery(OntologyOperation.loadQueryFromFile("queries/4-Chain.qr"));

		SWANTLogger.severen("6-Cyclic.qr");
		testQuery(OntologyOperation.loadQueryFromFile("queries/6-Cyclic.qr"));

		SWANTLogger.severen("8-Cyclic.qr");
		testQuery(OntologyOperation.loadQueryFromFile("queries/8-Cyclic.qr"));

		SWANTLogger.severen("10-Chain-Star.qr");
		testQuery(OntologyOperation
				.loadQueryFromFile("queries/10-Chain-Star.qr"));

		SWANTLogger.severen("12-Chain-Star.qr");
		testQuery(OntologyOperation
				.loadQueryFromFile("queries/12-Chain-Star.qr"));

		SWANTLogger.severen("Query-2.qr");
		testQuery(OntologyOperation.loadQueryFromFile("queries/Query-2.qr"));

		SWANTLogger.severen("Query-9.qr");
		testQuery(OntologyOperation.loadQueryFromFile("queries/Query-9.qr"));

	}

	@SuppressWarnings("unused")
	private static void commutativeTest() {
		SWANTLogger.severen("Query-1");
		testQuery(OntologyOperation
				.loadQueryFromFile("queries/commutative/Query-1.qr"));
		SWANTLogger.severen("Query-1-reverse");
		testQuery(OntologyOperation
				.loadQueryFromFile("queries/commutative/Query-1-reverse.qr"));
	}
}
