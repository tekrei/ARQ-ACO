package arqaco.statistics.impl;

import stocker.heuristic.OptimalNoStats;
import arqaco.statistics.CostCalculator;
import arqaco.utility.OntologyOperation;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class StockerCostCalculator extends CostCalculator {
	private OptimalNoStats ons;

	public StockerCostCalculator() {
		Graph graph = OntologyOperation.getGraph();
		ons = new OptimalNoStats(graph);
	}

	@Override
	public double getCost(Triple t) {
		return ons.getCost(t);
	}

	@Override
	public double getJoinCost(Triple t1, Triple t2) {
		return ons.getCost(t1, t2);
	}
}
