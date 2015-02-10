package arqaco.ai.aco;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import arqaco.ai.Optimization;
import arqaco.ai.ds.ACOParameters;
import arqaco.ai.ds.TPV;
import arqaco.ds.TransformParameters;
import arqaco.statistics.CostCalculator;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public abstract class AntColonyOptimization implements Optimization {
	protected double[][] phero;

	protected double[][] costs;

	protected boolean[][] joins;

	protected double[] tripleCosts;

	TPV tp;

	protected Vector<Ant> antz;

	protected Random random;

	protected int[] solutionPath;

	protected double solcost;

	CostCalculator cc = TransformParameters.getCostCalculator();

	public AntColonyOptimization() {
	}

	protected void initParameters() {
		antz = new Vector<Ant>();
		phero = new double[ACOParameters.graphSize][ACOParameters.graphSize];
		costs = new double[ACOParameters.graphSize][ACOParameters.graphSize];
		solutionPath = new int[ACOParameters.graphSize];
		solcost = 0.0;
		random = new Random();
	}

	protected void updateSolPath(Ant z) {
		if ((solcost > z.pathcost) || (solcost == 0.0)) {
			for (int i = 0; i < z.path.length; i++) {
				solutionPath[i] = z.path[i];
			}
			solcost = z.pathcost;
		}
	}

	protected void calculateAntTravelCost(Ant z) {
		double cost = 0;
		for (int i = 0; i < z.path.length - 1; i++) {
			int left = z.path[i];
			int right = z.path[i + 1];
			if (right == -1)// initvalue
				break;
			cost += getCost(z, left, right);
		}
		z.pathcost = cost;

	}

	protected double getCost(Ant z, int from, int to) {
		return costs[from][to];
	}

	protected double getCostNormal(Ant z, int from, int to) {
		double cost = costs[from][to];
		double tCost;
		for (int i = 0; i < z.pathIndex; i++) {
			if (z.path[i] == to)
				break;
			if (joins[z.path[i]][to]) {
				tCost = costs[z.path[i]][to];
				if (tCost < cost)
					cost = tCost;
			}
		}
		return cost;
	}

	protected int getCorrectJoinCount(Ant z) {
		int joinCount = 0;
		for (int i = 0; i < z.path.length - 1; i++) {
			if (joins[z.path[i]][z.path[i + 1]])
				joinCount++;
		}
		return joinCount;
	}

	public static int random(int min, int max) {
		return min + (int) (Math.random() * max);
	}

	protected void initSystem(TPV parts) {
		tp = parts;
		this.costs = parts.cost;
		for (double[] row : this.phero) {
			Arrays.fill(row, ACOParameters.minPh);
		}
	}

	@Override
	public abstract int[] executeAlgorithm(TPV parts);

	protected abstract void pheromenDeposit(Ant z);

	protected abstract void initAntz(int startPos);

	protected abstract void updateSystem();

	protected abstract int getNextCity(Ant z, TPV graph);

	protected abstract void evaporate();

	protected abstract double antProduct(Ant z, int from, int to);

}
