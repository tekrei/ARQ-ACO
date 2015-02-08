package arqaco.transform;

import stocker.core.BasicPatternGraph;
import stocker.heuristic.HeuristicBasicPattern;
import stocker.heuristic.OptimalNoStats;
import stocker.heuristic.ProbabilisticFrameworkJoin;
import stocker.heuristic.VariableCounting;
import stocker.heuristic.VariableCountingUnbound;
import arqaco.utility.OntologyOperation;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformBase;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.core.BasicPattern;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class StockerTransform extends TransformBase {

	public static StockerTransform VC = new StockerTransform(
			new VariableCounting());
	public static StockerTransform VCP = new StockerTransform(
			new VariableCountingUnbound());
	public static StockerTransform ONS = new StockerTransform(
			new OptimalNoStats(OntologyOperation.getGraph()));
	public static StockerTransform PFJ = new StockerTransform(
			new ProbabilisticFrameworkJoin(
					OntologyOperation.getExecutionContext()));

	HeuristicBasicPattern heuristic;

	public StockerTransform(HeuristicBasicPattern _heuristic) {
		heuristic = _heuristic;
	}

	@Override
	public Op transform(OpProject opp, Op subOp) {
		OpBGP opb = new OpBGP(reorderTriples(((OpBGP) subOp).getPattern()));
		return new OpProject(opb, opp.getVars());
	}

	private BasicPattern reorderTriples(BasicPattern pattern) {
		BasicPatternGraph basicPatternGraph = new BasicPatternGraph(pattern,
				heuristic);
		// Optimize the abstracted graph and return the optimized BasicPattern
		return basicPatternGraph.optimize();
	}
}