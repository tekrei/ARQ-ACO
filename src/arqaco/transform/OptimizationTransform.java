package arqaco.transform;

import arqaco.ai.Optimization;
import arqaco.ai.ds.TPV;
import arqaco.ds.TransformParameters;
import arqaco.statistics.impl.VCEstimator;

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
public class OptimizationTransform extends TransformBase {

	private Optimization optimization = TransformParameters.getOptimizator();
	private VCEstimator estimator = new VCEstimator();

	@Override
	public Op transform(OpProject opp, Op subOp) {
		OpBGP opb = new OpBGP(reorderTriples(((OpBGP) subOp).getPattern()));
		return new OpProject(opb, opp.getVars());
	}

	private BasicPattern reorderTriples(BasicPattern pattern) {
		// if there is only 1 pattern don't reorder it
		if (pattern.size() < 2)
			return pattern;
		// prepare triples for Ant System
		TPV tp = estimator.getTripleValues(pattern);
		// Execute ant system
		int[] newTripleOrder = optimization.executeAlgorithm(tp, tp.least);
		// Now reorder triples
		BasicPattern newPattern = new BasicPattern();
		for (int i = 0; i < newTripleOrder.length; i++) {
			newPattern.add(pattern.get(newTripleOrder[i]));
		}
		return newPattern;
	}
}
