package test;

import java.io.File;
import java.util.Arrays;

import arqaco.ds.Constants;
import arqaco.ds.QueryInfo;
import arqaco.ds.QueryResult;
import arqaco.ds.TransformParameters;
import arqaco.transform.OptimizationTransform;
import arqaco.transform.ReorderFixedTransformation;
import arqaco.transform.ReorderWeightedTransformation;
import arqaco.transform.StockerTransformONS;
import arqaco.transform.StockerTransformPFJ;
import arqaco.utility.OntologyOperation;
import arqaco.utility.QRFilter;
import arqaco.utility.SWANTLogger;

import com.hp.hpl.jena.sparql.algebra.Transform;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class ExecuteQueries {

	public static int execCount = 1;

	ExecuteQueries invoker;

	public static void folderTest() {

	}

	/**
	 * Method for executing the folder containing queries
	 * 
	 * @param folderName
	 *            folder name which contains the files of the queries
	 */
	public static void executeFiles(String folderName) {
		File folder = new File(folderName);

		File[] files = folder.listFiles(QRFilter.QRFF);

		Arrays.sort(files);

		for (File f : files) {
			if (f.isFile()) {
				String fileName = f.getAbsolutePath();
				SWANTLogger.severen("\n[" + fileName + "]");
				testQuery(OntologyOperation.loadQueryFromFile(fileName));
			}
		}
	}

	protected static void testQuery(QueryInfo qi) {
		SWANTLogger.fine("RESULT COUNT:" + qi.resultCount);
		invoke(qi);
	}

	private static void invoke(QueryInfo qi) {
		normalExecution(qi);
		reorderWExecution(qi);
		reorderFExecution(qi);
		stockerONSExecution(qi);
		stockerPFJExecution(qi);
		antSystemVCMRExecution(qi);
		elitistAntSystemExecution(qi);
		maxminAntSystemExecution(qi);
	}

	public static void maxminAntSystemExecution(QueryInfo qi) {
		SWANTLogger.severe("MMAS " + (SWANTLogger.getCurrentTime()) + "\t");
		TransformParameters.optimization = Constants.OPT_MMAS;
		TransformParameters.cost = Constants.COST_MOD_STOCKER;
		TransformParameters.nodechooser = Constants.SNC_RND;
		execute(qi.query, new OptimizationTransform());
	}

	public static void elitistAntSystemExecution(QueryInfo qi) {
		SWANTLogger.severe("EAS " + (SWANTLogger.getCurrentTime()) + "\t");
		TransformParameters.optimization = Constants.OPT_EAS;
		TransformParameters.cost = Constants.COST_MOD_STOCKER;
		TransformParameters.nodechooser = Constants.SNC_RND;
		execute(qi.query, new OptimizationTransform());
	}

	private static void antSystemVCMRExecution(QueryInfo qi) {
		SWANTLogger.severe("AS " + (SWANTLogger.getCurrentTime()) + "\t");
		TransformParameters.optimization = Constants.OPT_AS;
		TransformParameters.cost = Constants.COST_MOD_STOCKER;
		TransformParameters.nodechooser = Constants.SNC_RND;
		execute(qi.query, new OptimizationTransform());
	}

	private static void reorderFExecution(QueryInfo qi) {
		SWANTLogger.severe("RF " + (SWANTLogger.getCurrentTime()) + "\t");
		execute(qi.query, new ReorderFixedTransformation());
	}

	private static void reorderWExecution(QueryInfo qi) {
		SWANTLogger.severe("RW " + (SWANTLogger.getCurrentTime()) + "\t");
		execute(qi.query, new ReorderWeightedTransformation());
	}

	private static void stockerONSExecution(QueryInfo qi) {
		SWANTLogger.severe("ARQ/ONS " + (SWANTLogger.getCurrentTime()) + "\t");
		execute(qi.query, new StockerTransformONS());
	}

	private static void stockerPFJExecution(QueryInfo qi) {
		SWANTLogger.severe("ARQ/PFJ " + (SWANTLogger.getCurrentTime()) + "\t");
		execute(qi.query, new StockerTransformPFJ());
	}

	private static void normalExecution(QueryInfo qi) {
		SWANTLogger.severe("NE " + (SWANTLogger.getCurrentTime()) + "\t");
		execute(qi.query, null);
	}

	public static long execute(String query, Transform ot) {
		long totalTime = 0;
		long minimumTime = Long.MAX_VALUE;
		long maximumTime = 0;
		for (int i = 0; i < execCount; i++) {
			long startTime = System.currentTimeMillis();
			// break the loop if query timeout occurs
			QueryResult queryResult;
			if (ot == null) {
				queryResult = OntologyOperation.executeQuery(query);
			} else {
				queryResult = OntologyOperation.executeQuery(query, ot);
			}
			if (queryResult.results == null) {
				// break the loop if query timeout occurs
				break;
			}
			long tourTime = System.currentTimeMillis() - startTime;
			if (tourTime < minimumTime)
				minimumTime = tourTime;
			if (tourTime > maximumTime)
				maximumTime = tourTime;
			totalTime += tourTime;
			SWANTLogger.severe(i + "(" + tourTime + ") ");
		}
		long avgTime = totalTime / execCount;
		SWANTLogger.severen("AVERAGE TIME (" + execCount + "):" + avgTime
				+ " [MIN:" + minimumTime + " MAX:" + maximumTime + "]");
		return avgTime;
	}

	public ExecuteQueries() {
		super();
	}

}