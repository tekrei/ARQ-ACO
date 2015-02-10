package arqaco.ai.aco;

import arqaco.ai.ds.ACOParameters;
import arqaco.ai.ds.TPV;

/**
 * 
 * Ant System algorithm proposed by Marco Dorigo
 * 
 * @author E. Guzel Kalayci
 */
public class AntSystem extends AntColonyOptimization {

	static int first = 0;
	static int second = 0;
	static int third = 0;

	public AntSystem() {
		super();
	}

	@Override
	public int[] executeAlgorithm(TPV parts) {
		return executeAlgorithm(parts, -1);
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
			}
			updateSystem();
		}
		return solutionPath;
	}

	@Override
	protected void pheromenDeposit(Ant z) {
		for (int i = 0; i < z.path.length - 1; i++) {
			int left = z.path[i];
			int right = z.path[i + 1];
			if (right == -1)
				break;
			phero[left][right] += ACOParameters.Q / z.pathcost;
		}
	}

	@Override
	protected void initAntz(int startPos) {
		int rndPopSize = ACOParameters.populationSize;
		if (startPos > -1) {
			rndPopSize = (int) (ACOParameters.populationSize * ACOParameters.randomStartPointFactor);
		}
		int counter = 0;
		for (int i = 0; i < ACOParameters.populationSize; i++) {
			if (counter < rndPopSize) {
				counter++;
				addAnt(getRandomNode(), true);
			} else {
				addAnt(startPos, false);
			}
		}
	}

	private void addAnt(int startPos, boolean rndAnt) {
		Ant z = new Ant(ACOParameters.graphSize, startPos, rndAnt);
		antz.addElement(z);
	}

	public static int getRandomNode() {
		return random(0, ACOParameters.graphSize);
	}

	@Override
	protected void updateSystem() {
		evaporate();
		for (Ant z : antz) {
			calculateAntTravelCost(z);
			pheromenDeposit(z);
			updateSolPath(z);
			z.initPath();
		}
	}

	@Override
	protected int getNextCity(Ant z, TPV graph) {
		int from, to;
		double denom = 1.0;

		from = z.getPosition();

		for (to = 0; to < graph.size(); to++) {
			if ((!z.visited(to)) && (from != to)) {
				denom += antProduct(z, from, to);
			}
		}

		to = 0;

		int selectedcity = -1;

		do {

			double _p;

			if ((!z.visited(to)) && (from != to)) {
				_p = antProduct(z, from, to) / denom;

				if ((_p > 0.0) && (random.nextDouble() < _p)) {
					selectedcity = to;
					break;
				}
			}
			to++;
		} while (to < graph.size());

		int nonvisitedlast = -1;
		if (selectedcity < 0) {
			to = 0;
			double prob = 0.0;
			do {

				double _p;

				if (!z.visited(to) && (from != to) && (to != ACOParameters.end)) {
					nonvisitedlast = to;
					double ap = antProduct(z, from, to);
					_p = ap / denom;
					if (_p > prob) {
						selectedcity = to;
						prob = _p;
					}
				}
				to++;
			} while (to < graph.size());
		}
		if (selectedcity < 0) {
			selectedcity = nonvisitedlast;
		} else {
		}
		return selectedcity;
	}

	@Override
	protected void evaporate() {

		for (int i = 0; i < ACOParameters.graphSize; i++) {
			for (int j = 0; j < ACOParameters.graphSize; j++) {
				if (i != j) {
					phero[i][j] *= (1 - ACOParameters.p);
				}
			}
		}
	}

	@Override
	protected double antProduct(Ant z, int from, int to) {
		return (Math.pow(phero[from][to], ACOParameters.alpha) * Math.pow(
				(1.0 / getCost(z, from, to)), ACOParameters.beta));
	}

	@Override
	public int getStart() {
		return ACOParameters.start;
	}

	@Override
	public int getEnd() {
		return ACOParameters.end;
	}
}
