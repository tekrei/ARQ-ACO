package arqaco.statistics.impl;

import java.util.List;

import stocker.core.BasicPatternJoin;
import arqaco.statistics.CostCalculator;
import arqaco.utility.OntologyOperation;
import arqaco.utility.SWANTLogger;

import com.hp.hpl.jena.graph.Triple;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class ModifiedStockerCostCalculator extends CostCalculator {

	@Override
	public double getCost(Triple t) {
		return getCardinality(t) / OntologyOperation.size;

	}

	public double getCardinality(Triple t) {
		return CardinalityCalculator.getEstimatedCardinality(t,
				OntologyOperation.size);
	}

	/**
	 * Return cost of the two triples
	 */
	@Override
	public double getJoinCost(Triple triple1, Triple triple2) {
		// non-commutative
		return getCost(triple1) + getTripleCosts(triple1, triple2);
	}

	/**
	 * The method estimates the execution cost of joined triple patterns based
	 * on simple heuristics. Note that the bound predicate-predicate join is
	 * Explicitly weighted 1.0 to avoid that the optimizer chooses certain
	 * patterns as "good" optimizations.
	 * 
	 * @author Marcus Stocker
	 * 
	 *         Changed by Elem Guzel at 11 April 2012
	 * 
	 * @param triple1
	 * @param triple2
	 * @return Double
	 */
	public double getTripleCosts(Triple triple1, Triple triple2) {
		double p1 = getCost(triple1);
		double p2 = getCost(triple2);
		if (!isJoined(triple1, triple2)) {
			return 1;
		}

		double cost = 32d;
		int MAX_COST = 32;

		List<String> joins = BasicPatternJoin.specificTypes(triple1, triple2); // List<String>

		for (String type : joins) {
			// Weight bound PP joins with 1.0, always
			// if (type.equals(BasicPatternJoin.bPP))
			// return cost / MAX_COST;

			if (type.equals(BasicPatternJoin.uSS))
				cost -= 2;
			else if (type.equals(BasicPatternJoin.uSP))
				cost -= 3;
			else if (type.equals(BasicPatternJoin.uSO))
				cost -= 1;
			else if (type.equals(BasicPatternJoin.uPS))
				cost -= 3;
			else if (type.equals(BasicPatternJoin.uPP))
				cost -= 3;
			else if (type.equals(BasicPatternJoin.uPO))
				cost -= 3;
			else if (type.equals(BasicPatternJoin.uOS))
				cost -= 2;// instead of 1 in VCP
			else if (type.equals(BasicPatternJoin.uOP))
				cost -= 3;
			else if (type.equals(BasicPatternJoin.uOO))
				cost -= 1;
			else if (type.equals(BasicPatternJoin.bSS))
				cost -= 2 * 2;
			else if (type.equals(BasicPatternJoin.bSP))
				cost -= 3 * 2;
			else if (type.equals(BasicPatternJoin.bSO))
				cost -= 1 * 2;
			else if (type.equals(BasicPatternJoin.bPS))
				cost -= 3 * 2;
			else if (type.equals(BasicPatternJoin.bPO))
				cost -= 3 * 2;
			else if (type.equals(BasicPatternJoin.bOS))
				cost -= 2 * 2;// instead of 1*2 in VCP
			else if (type.equals(BasicPatternJoin.bOP))
				cost -= 3 * 2;
			else if (type.equals(BasicPatternJoin.bOO))
				cost -= 1 * 2;
		}
		double c = cost / MAX_COST;
		SWANTLogger.info("cost:" + c + " p1:" + p1 + " p2:" + p2);
		return c * p1 * p2;
	}

	/**
	 * The method returns true if two Triple are joined, because of matching
	 * S/P/O. The method returns true, if one of the nine possible combinations
	 * matches.
	 * 
	 * @param triple1
	 * @param triple2
	 * @return boolean
	 */
	public static boolean isJoined(Triple triple1, Triple triple2) {
		if (triple1.subjectMatches(triple2.getSubject()))
			return true;
		if (triple1.subjectMatches(triple2.getPredicate()))
			return true;
		if (triple1.subjectMatches(triple2.getObject()))
			return true;
		if (triple1.predicateMatches(triple2.getSubject()))
			return true;
		if (triple1.predicateMatches(triple2.getObject()))
			return true;
		if (triple1.objectMatches(triple2.getSubject()))
			return true;
		if (triple1.objectMatches(triple2.getPredicate()))
			return true;
		if (triple1.objectMatches(triple2.getObject()))
			return true;
		if (triple1.predicateMatches(triple2.getPredicate())) {
			// CHANGE by Elem Guzel KALAYCI 10.06.2012
			if (triple1.getPredicate().getLocalName().equals("type")
					|| triple1.getPredicate().getLocalName()
							.equals("subClassOf"))
				return false;
			// END OF FIX
			return true;
		}
		return false;
	}

}
