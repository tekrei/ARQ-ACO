package arqaco.transform;

import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.TransformBase;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.engine.optimizer.reorder.ReorderLib;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class ReorderFixedTransformation extends TransformBase {

	public ReorderFixedTransformation() {
	}

	@Override
	public Op transform(OpProject opp, Op subOp) {
		OpBGP opb = new OpBGP(reorderTriples(((OpBGP) subOp).getPattern()));
		return new OpProject(opb, opp.getVars());
	}

	private BasicPattern reorderTriples(BasicPattern pattern) {
		com.hp.hpl.jena.sparql.engine.optimizer.reorder.ReorderTransformation rf = ReorderLib
				.fixed();
		return rf.reorder(pattern);
	}
}
