package test;

import arqaco.ds.Constants;
import arqaco.ds.QueryInfo;
import arqaco.ds.TransformParameters;
import arqaco.transform.OptimizationTransform;
import arqaco.transform.ReorderFixedTransformation;
import arqaco.transform.StockerTransformONS;
import arqaco.transform.StockerTransformPFJ;
import arqaco.utility.OntologyOperation;
import arqaco.utility.SWANTLogger;

/**
 * Class to test individual or specific queries
 * required for the article
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class LUBMQueryExecutionTest {

	public static void main(String[] args) throws Exception {
		OntologyOperation.loadOntology("data/univ-bench.owl");
		OntologyOperation.loadOntology("data/university_v2.zip");

		// singleQueryTest();
		// articleTest();
		// stockerTimeTest();
		ontologySize();
	}

	/**
	 * Method to find the query size
	 */
	private static void ontologySize() {
		SWANTLogger
				.severen("Ontology size:"
						+ OntologyOperation.executeQuery(OntologyOperation
								.loadQueryFromFile("queries/article/rdfSize.qr").query).resultCount);
	}

	@SuppressWarnings("unused")
	private static void stockerTimeTest() {
		OntologyOperation
				.executeQuery(OntologyOperation
						.loadQueryFromFile("queries/Query-8.qr").query,
						new StockerTransformONS());
		OntologyOperation
				.executeQuery(OntologyOperation
						.loadQueryFromFile("queries/Query-8.qr").query,
						new StockerTransformONS());
	}

	@SuppressWarnings("unused")
	private static void articleTest() throws Exception {
		QueryInfo qi = OntologyOperation
				.loadQueryFromFile("queries/article/0-beforeopt.qr");
		OntologyOperation.executeQuery(qi.query);
		SWANTLogger.severen("BEFORE OPT");
		qi = OntologyOperation
				.loadQueryFromFile("queries/article/0-beforeopt.qr");
		OntologyOperation.executeQuery(qi.query);
		SWANTLogger.severen("AFTER OPT");
		qi = OntologyOperation
				.loadQueryFromFile("queries/article/1-afteropt.qr");
		OntologyOperation.executeQuery(qi.query);
	}

	@SuppressWarnings("unused")
	private static void singleQueryTest() throws Exception {
		QueryInfo qi = OntologyOperation
				.loadQueryFromFile("queries/Query-8.qr");
		OntologyOperation.executeQuery(qi.query,
				new ReorderFixedTransformation());
		OntologyOperation.executeQuery(qi.query, new StockerTransformPFJ());
		SWANTLogger.severe("STOPF " + (SWANTLogger.getCurrentTime()) + "\t");
		long startTime = System.currentTimeMillis();
		OntologyOperation.executeQuery(qi.query, new StockerTransformPFJ());
		SWANTLogger
				.severe("(" + (System.currentTimeMillis() - startTime) + ")");
		SWANTLogger.severe("\nORDF " + (SWANTLogger.getCurrentTime()) + "\t");
		startTime = System.currentTimeMillis();
		OntologyOperation.executeQuery(qi.query,
				new ReorderFixedTransformation());
		SWANTLogger
				.severe("(" + (System.currentTimeMillis() - startTime) + ")");
		SWANTLogger.severe("\nAS " + (SWANTLogger.getCurrentTime()) + "\t");
		startTime = System.currentTimeMillis();
		TransformParameters.optimization = Constants.OPT_AS;
		TransformParameters.cost = Constants.COST_MOD_STOCKER;
		TransformParameters.nodechooser = Constants.SNC_RND;
		OntologyOperation.executeQuery(qi.query, new OptimizationTransform());
		SWANTLogger
				.severe("(" + (System.currentTimeMillis() - startTime) + ")");
	}

}
