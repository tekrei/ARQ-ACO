package arqaco.ai.aco;

import arqaco.ai.ds.ACOParameters;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class ElitistAntSystem extends AntSystem {

	private void depositPheromene_IterationBest() {
		for (int i = 0; i < solutionPath.length - 2; i++) {
			int left = solutionPath[i];
			int right = solutionPath[i + 1];
			if (right == -1)
				break;
			phero[left][right] += ACOParameters.Q / solcost;
		}
	}

	@Override
	protected void updateSystem() {
		super.updateSystem();
		depositPheromene_IterationBest();
	}

}
