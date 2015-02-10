package arqaco.statistics.impl;

import arqaco.statistics.CostCalculator;
import arqaco.utility.OntologyOperation;

import com.hp.hpl.jena.graph.Triple;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class CardinalityCostCalculator extends CostCalculator {

	public CardinalityCostCalculator() {
	}

	@Override
	public double getCost(Triple t) {
		return CardinalityCalculator.getEstimatedCardinality(t,
				OntologyOperation.size);
	}

	@Override
	public double getJoinCost(Triple t1, Triple t2) {
		return CardinalityCalculator.getEstimatedCardinality(t1,
				OntologyOperation.size)
				* CardinalityCalculator.getEstimatedCardinality(t2,
						OntologyOperation.size);
	}
}
