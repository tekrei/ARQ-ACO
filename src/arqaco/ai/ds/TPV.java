package arqaco.ai.ds;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.BasicPattern;

/**
 * 
 * @author kalayci
 * 
 */
public class TPV {

	public double tripleCost[];
	public double cost[][];
	public BasicPattern pattern;

	public int least;

	public int size() {
		return cost.length;
	}

	public double getTripleCost(int index) {
		return tripleCost[index];
	}

	public double getCost(int i, int j) {
		return cost[i][j];
	}

	public void printCosts() {
		for (int i = 0; i < cost.length; i++) {
			for (int j = 0; j < cost.length; j++) {
				System.out.println("cost(" + i + "," + j + "):" + cost[i][j]);
			}
		}
	}

	public Triple getTriple(int index) {
		return pattern.get(index);
	}
}