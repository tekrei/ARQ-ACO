package arqaco.transform;

import stocker.core.BasicPatternGraph;
import stocker.heuristic.OptimalNoStats;
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
public class StockerTransformONS extends TransformBase {

	public StockerTransformONS() {
	}

	@Override
	public Op transform(OpProject opp, Op subOp) {
		OpBGP opb = new OpBGP(reorderTriples(((OpBGP) subOp).getPattern()));
		return new OpProject(opb, opp.getVars());
	}

	private BasicPattern reorderTriples(BasicPattern pattern) {
		BasicPatternGraph basicPatternGraph = new BasicPatternGraph(pattern,
				new OptimalNoStats(OntologyOperation.getGraph()));
		// Optimize the abstracted graph and return the optimized BasicPattern
		return basicPatternGraph.optimize();

	}
}