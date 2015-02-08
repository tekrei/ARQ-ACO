package arqaco.transform;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformBase;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.engine.optimizer.StatsMatcher;
import com.hp.hpl.jena.sparql.engine.optimizer.reorder.ReorderWeighted;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class ReorderWeightedTransformation extends TransformBase {

	public ReorderWeightedTransformation() {
	}

	@Override
	public Op transform(OpProject opp, Op subOp) {
		OpBGP opb = new OpBGP(reorderTriples(((OpBGP) subOp).getPattern()));
		return new OpProject(opb, opp.getVars());
	}

	private BasicPattern reorderTriples(BasicPattern pattern) {
		return new ReorderWeighted(new StatsMatcher()).reorder(pattern);
	}
}
