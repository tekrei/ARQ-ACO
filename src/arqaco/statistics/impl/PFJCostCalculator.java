package arqaco.statistics.impl;

import java.util.List;

import stocker.core.BasicPatternJoin;
import stocker.probability.Probability;
import arqaco.statistics.CostCalculator;
import arqaco.utility.OntologyOperation;

import com.hp.hpl.jena.graph.Triple;

/**
 * 
 * @author kalayci
 * 
 */
public class PFJCostCalculator extends CostCalculator {

	Probability probability;

	public PFJCostCalculator() {
		probability = OntologyOperation.probability;
	}

	/**
	 * This method returns the cost for the triple pattern as the probability
	 * returned by the probabilistic framework.
	 * 
	 * @param triple1
	 * @return double
	 */
	public double getCost(Triple triple1) {
		if (probability == null)
			throw new NullPointerException(
					"The probability framework has not been set to the ARQ context!");

		return probability.getProbability(triple1);
	}

	/**
	 * Return cost of the two triples
	 */
	@Override
	public double getJoinCost(Triple triple1, Triple triple2) {
		return getCost(triple1) + getTripleCosts(triple1, triple2);
	}

	/**
	 * This method returns the cost for the triple patterns as the probability
	 * returned by the probabilistic framework.
	 * 
	 * @param triple1
	 * @param triple2
	 * @return double
	 */
	public double getTripleCosts(Triple triple1, Triple triple2) {
		if (probability == null)
			throw new NullPointerException(
					"The probability framework has not been set to the ARQ context!");

		// Get the type of specific joins
		List<?> joins = BasicPatternJoin.specificTypes(triple1, triple2);

		if (joins.contains(BasicPatternJoin.bPP))
			return 1.0;

		return probability.getProbability(triple1)
				+ probability.getProbability(triple1, triple2);
	}

	@Override
	public double getTripleCost(Triple triple1, Triple triple2, double p1) {
		if (probability == null)
			throw new NullPointerException(
					"The probability framework has not been set to the ARQ context!");

		// Get the type of specific joins
		List<?> joins = BasicPatternJoin.specificTypes(triple1, triple2);

		if (joins.contains(BasicPatternJoin.bPP))
			return 1.0;

		return probability.getProbability(triple1)
				+ probability.getProbability(triple1, triple2);
	}
}
