package arqaco.ai.ds;

import arqaco.utility.SWANTLogger;

/**
 * 
 * @author kalayci
 * 
 */
public class ACOParameters {

	private ACOParameters() {
	}

	// node count
	public static int graphSize;
	// ant count
	public static int populationSize = 100;
	// factor of randomly starting ants
	public static float randomStartPointFactor = 0.5f;
	// maximum pheromone
	public static double maxPh = 10;
	// minimum pheromone
	public static double minPh = 0.001;
	public static int iteration = 100;
	public static int elitistCount = 1;
	// alpha must be <=1
	public static double alpha = 1;
	// beta must be >=1
	public static double beta = 2;
	// special pheromone parameter
	public static double Q = 1;
	// Evaporation rate
	public static double p = 0.5;
	// MAX-MIN tMIN parameter, must be >=1
	public static double a = 10;
	// MAX-MIN iteration reinit parameter
	public static int reinitIterationCount = iteration / 10;
	// starting node
	public static int start = -1;
	// end node
	public static int end = -1;

	public static void printParameters() {
		SWANTLogger.severen("populationSize:" + populationSize);
		SWANTLogger.severen("iteration:" + iteration);
		SWANTLogger.severen("alpha:" + alpha);
		SWANTLogger.severen("beta:" + beta);
		SWANTLogger.severen("p:" + p);
		SWANTLogger.severen("Q:" + Q);
		// EAS parameters
		SWANTLogger.severen("elitistCount:" + elitistCount);
		// MMAS parameters
		SWANTLogger.severen("maxPh:" + maxPh);
		SWANTLogger.severen("minPh:" + minPh);
		SWANTLogger.severen("reinitIterationCount:" + reinitIterationCount);
		SWANTLogger.severen("a:" + a);
	}
}
