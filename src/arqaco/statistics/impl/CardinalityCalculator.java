package arqaco.statistics.impl;

import arqaco.utility.OntologyOperation;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class CardinalityCalculator {

	public static long getEstimatedCardinality(Triple triple, long ontSize) {
		return getMINCardinality(triple, ontSize);
	}

	private static long getMINCardinality(Triple triple, long ontSize) {
		if (triple.getPredicate().getLocalName().equals("subClassOf"))
			return ontSize;

		long minCar = ontSize;

		long cardinality = OntologyOperation
				.getModel()
				.getGraph()
				.getStatisticsHandler()
				.getStatistic(triple.getSubject(), triple.getPredicate(),
						triple.getObject());

		if (cardinality == -1) {
			if (triple.getSubject().isConcrete()) {
				cardinality = OntologyOperation.getModel().getGraph()
						.getStatisticsHandler()
						.getStatistic(triple.getSubject(), Node.ANY, Node.ANY);
				if (cardinality < minCar)
					minCar = cardinality;
			}

			if (triple.getPredicate().isConcrete()) {
				if (!triple.getPredicate().getLocalName().equals("type")) {
					cardinality = OntologyOperation
							.getModel()
							.getGraph()
							.getStatisticsHandler()
							.getStatistic(Node.ANY, triple.getPredicate(),
									Node.ANY);
					if (cardinality < minCar)
						minCar = cardinality;
				}
			}

			if (triple.getObject().isConcrete()) {
				cardinality = OntologyOperation.getModel().getGraph()
						.getStatisticsHandler()
						.getStatistic(Node.ANY, Node.ANY, triple.getObject());
				if (cardinality < minCar)
					minCar = cardinality;
			}
			cardinality = minCar;
		}
		return cardinality;
	}
}
