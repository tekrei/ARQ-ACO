package arqaco.ai.aco;

import arqaco.ai.ds.ACOParameters;
import arqaco.ai.ds.TPV;

public class MaxMinAntSystem extends AntSystem {

	Ant globalbest;
	Ant iterationbest;
	int bestNotFound;
	double tMAX;
	double tMIN;

	@Override
	protected void initAntz(int startPos) {
		super.initAntz(startPos);
		// select global best
		tMAX = ACOParameters.maxPh;
		tMIN = ACOParameters.minPh;
		pheromoneInit();
	}

	private void pheromoneInit() {
		int length = phero.length;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				phero[i][j] = tMAX;
			}
		}
	}

	private void depositPheromene(Ant iterationbest) {
		for (int i = 0; i < iterationbest.path.length - 2; i++) {
			int left = iterationbest.path[i];
			int right = iterationbest.path[i + 1];
			if (right == -1)
				break;
			phero[left][right] += ACOParameters.Q / iterationbest.pathcost;
			if (phero[left][right] > tMAX) {
				phero[left][right] = tMAX;
			}
		}
	}

	@Override
	protected void evaporate() {

		for (int i = 0; i < ACOParameters.graphSize; i++) {
			for (int j = 0; j < ACOParameters.graphSize; j++) {
				if (i != j) {
					phero[i][j] *= (1 - ACOParameters.p);

					if (phero[i][j] < tMIN)
						phero[i][j] = tMIN;

				}
			}
		}
	}

	private void calculateMAXMIN(double bestCost) {
		tMAX = 1 / (ACOParameters.p * bestCost);
		tMIN = tMAX / ACOParameters.a;
	}

	@Override
	public int[] executeAlgorithm(TPV parts, int start) {
		ACOParameters.graphSize = parts.size();
		initParameters();
		initSystem(parts);
		ACOParameters.start = start;
		initAntz(ACOParameters.start);
		for (int t = 0; t < ACOParameters.iteration; t++) {
			for (int k = 0; k < ACOParameters.populationSize; k++) {
				Ant z = antz.get(k);
				while (z.pathIndex < ACOParameters.graphSize) {
					int newPos = getNextCity(z, parts);
					z.moveTo(newPos);
				}
				// calculate path cost for ant z
				calculateAntTravelCost(z);
				// is this the best ant?
				if (iterationbest == null) {
					iterationbest = z.clone();
				}
				if (Double.compare(iterationbest.pathcost, z.pathcost) > 0) {
					iterationbest = z.clone();
				}
				// reset ant for next tour
				antz.get(k).initPath();
			}
			updateSystem();
			// deposit on the iteration best tour
			depositPheromene(iterationbest);
		}
		return globalbest.path;
	}

	@Override
	protected void calculateAntTravelCost(Ant z) {
		double cost = 0;
		for (int i = 0; i < z.path.length - 1; i++) {
			int left = z.path[i];
			int right = z.path[i + 1];
			if (right == -1)
				break;
			cost += getCost(z, left, right);
		}
		z.pathcost = cost;
	}

	@Override
	protected void updateSystem() {
		// first evaporate
		evaporate();
		// is global best tour achieved?
		if (globalbest == null) {
			globalbest = iterationbest.clone();
		} else if (globalbest.pathcost > iterationbest.pathcost) {
			globalbest = iterationbest.clone();
			calculateMAXMIN(globalbest.pathcost);
			bestNotFound = 0;
		} else {
			// no best found for bestNotFound tours
			bestNotFound++;
		}
		if (ACOParameters.reinitIterationCount > 0) {
			if (bestNotFound >= ACOParameters.reinitIterationCount) {
				pheromoneInit();
				bestNotFound = 0;
			}
		}
	}
}
