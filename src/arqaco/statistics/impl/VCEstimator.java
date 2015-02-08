package arqaco.statistics.impl;

import arqaco.ai.ds.TPV;
import arqaco.ds.TransformParameters;
import arqaco.statistics.CostCalculator;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.BasicPattern;

/**
 * 
 * @author kalayci
 * 
 */
public class VCEstimator {

	public TPV getTripleValues(BasicPattern pattern) {
		CostCalculator cc = TransformParameters.getCostCalculator();
		TPV tp = new TPV();
		int size = pattern.size();
		double cost[][] = new double[size][size];

		for (int i = 0; i < size; i++) {
			Triple t1 = pattern.get(i);
			// SWANTLogger.info("t1:" + t1);
			for (int j = 0; j < size; j++) {
				Triple t2 = pattern.get(j);
				cost[i][j] = cc.getJoinCost(t1, t2);
				// SWANTLogger.info("\tt2:" + t2 + " c[" + i + "][" + j +
				// "]:"+cost[i][j]);
			}
		}
		// random node
		tp.least = -1;
		tp.cost = cost;
		tp.pattern = pattern;
		return tp;
	}
}