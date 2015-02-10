package arqaco.ds;

import arqaco.ai.Optimization;
import arqaco.ai.aco.AntSystem;
import arqaco.ai.aco.ElitistAntSystem;
import arqaco.ai.aco.MaxMinAntSystem;
import arqaco.statistics.CostCalculator;
import arqaco.statistics.impl.CardinalityCostCalculator;
import arqaco.statistics.impl.ModifiedStockerCostCalculator;
import arqaco.statistics.impl.PFJCostCalculator;
import arqaco.statistics.impl.StockerCostCalculator;
import arqaco.utility.SWANTLogger;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class TransformParameters {
	public static String nodechooser = Constants.SNC_CECN;
	public static String cost = Constants.COST_STOCKER;
	public static String optimization = Constants.OPT_AS;

	public static Optimization getOptimizator() {
		if (optimization.equals(Constants.OPT_MMAS))
			return new MaxMinAntSystem();
		if (optimization.equals(Constants.OPT_EAS))
			return new ElitistAntSystem();
		return new AntSystem();
	}

	public static CostCalculator getCostCalculator() {
		if (cost.equals(Constants.COST_MOD_STOCKER))
			return new ModifiedStockerCostCalculator();
		if (cost.equals(Constants.COST_STOCKER))
			return new StockerCostCalculator();
		if (cost.equals(Constants.COST_PFJ))
			return new PFJCostCalculator();
		return new CardinalityCostCalculator();
	}

	public static void printParameters() {
		SWANTLogger.severen("optimization:" + optimization);
		SWANTLogger.severen("nodechooser:" + nodechooser);
		SWANTLogger.severen("cost:" + cost);
	}
}
