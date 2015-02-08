package test;

import arqaco.ai.ds.ACOParameters;
import arqaco.ds.Constants;
import arqaco.ds.QueryInfo;
import arqaco.ds.TransformParameters;
import arqaco.transform.OptimizationTransform;
import arqaco.utility.OntologyOperation;
import arqaco.utility.SWANTLogger;

/**
 * Class for ACO parameters test
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class LUBMParametersTest extends ExecuteQueries {

	public static void main(String[] args) throws Exception {
		startTest();
	}

	private static void startTest() throws Exception {
		// Load ontologies
		OntologyOperation.loadOntology("data/univ-bench.owl");
		OntologyOperation.loadOntology("data/university_v2.zip");

		// how many times to run

		execCount = 10;

		// QueryInfo qi =
		// OntologyOperation.loadQueryFromFile("queries/10-Cyclic.qr");

		QueryInfo qi = OntologyOperation
				.loadQueryFromFile("queries/Query-9.qr");

		TransformParameters.cost = Constants.COST_MOD_STOCKER;
		TransformParameters.nodechooser = Constants.SNC_RND;

		SWANTLogger.severe("PS\tIT\tA\tB\tP\tAS\tEAS\tMMAS\n");

		int psArray[] = { 10, 25, 50, 100, 250 };

		for (int ps = 0; ps < psArray.length; ps++) {
			ACOParameters.populationSize = psArray[ps];
			for (int i = 50; i < 250; i += 50) {
				ACOParameters.iteration = i;
				for (int alpha = 1; alpha < 4; alpha++) {
					ACOParameters.alpha = alpha;
					for (int beta = 1; beta < 4; beta++) {
						ACOParameters.beta = beta;
						for (float p = 0.1f; p <= 0.51; p += 0.1) {
							ACOParameters.p = p;
							SWANTLogger.severe(psArray[ps] + "\t" + i + "\t"
									+ alpha + "\t" + beta + "\t" + p + "\t");
							TransformParameters.optimization = Constants.OPT_AS;
							SWANTLogger.severe(execute(qi.query,
									new OptimizationTransform()) + "\t");
							TransformParameters.optimization = Constants.OPT_EAS;
							SWANTLogger.severe(execute(qi.query,
									new OptimizationTransform()) + "\t");
							TransformParameters.optimization = Constants.OPT_MMAS;
							SWANTLogger.severe(execute(qi.query,
									new OptimizationTransform()) + "\t");
							SWANTLogger.severen("");
						}
					}
				}
			}
		}
	}
}
