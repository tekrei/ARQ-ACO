package arqaco.statistics.impl;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class StartNodeChooser {

	public static int getCheapestTriple(double[] cost) {
		double fv = Double.MAX_VALUE;
		int least = 0;
		int count = cost.length;
		for (int i = 0; i < count; i++) {
			if (cost[i] < fv) {
				least = i;
				fv = cost[i];
			}
		}
		return least;
	}

	public static int getCheapestNodeofCheapestEdge(double[][] c, double[] tc) {
		int size = tc.length;
		double fv = Double.MAX_VALUE;
		int least = -1;
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (c[i][j] < fv) {
					least = i;
					fv = c[i][j];
					if (tc[j] < tc[i]) {
						least = j;
						fv = c[j][i];
					}
				}
			}
		}
		return least;
	}
}
