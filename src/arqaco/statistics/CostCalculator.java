package arqaco.statistics;

import arqaco.ds.Constants;
import arqaco.ds.TransformParameters;
import arqaco.statistics.impl.StartNodeChooser;

import com.hp.hpl.jena.graph.Triple;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public abstract class CostCalculator {

	public abstract double getCost(Triple t);

	public abstract double getJoinCost(Triple t1, Triple t2);

	public double getTripleCost(Triple t1, Triple t2, double p) {
		return getJoinCost(t1, t2);
	}

	public int getLeast(double[][] c, double[] tc) {
		if (TransformParameters.nodechooser.equals(Constants.SNC_CECN)) {
			return StartNodeChooser.getCheapestNodeofCheapestEdge(c, tc);
		}
		if (TransformParameters.nodechooser.equals(Constants.SNC_CN)) {
			return StartNodeChooser.getCheapestTriple(tc);
		}
		return -1;
	}
}
