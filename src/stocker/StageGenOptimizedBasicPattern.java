/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package stocker;

import java.util.List;

import stocker.core.BasicPatternOptimizer;
import stocker.util.Config;
import stocker.util.Constants;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.mem.GraphMemBase;
import com.hp.hpl.jena.sparql.ARQException;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterBlockTriples;
import com.hp.hpl.jena.sparql.engine.main.StageGenerator;
//import com.hp.hpl.jena.sparql.engine.main.StageGeneratorGeneric;
import com.hp.hpl.jena.sparql.util.Context;

/**
 * The class implements the ARQ optimizer for basic graph patterns. Then
 * BasicPattern is statically optimized and a BasicStage is compiled out of the
 * optimized BasicPattern.
 * 
 * @author Markus Stocker
 * @author E. Guzel Kalayci 17 March 2012
 */

public class StageGenOptimizedBasicPattern implements StageGenerator {
	private StageGenerator other = null;
	private Config config = null;

	public StageGenOptimizedBasicPattern(StageGenerator other, Config config) {
		this.other = other;
		this.config = config;
	}

	/**
	 * The method compiles a BasicPattern into a StageList. The BasicPattern is
	 * statically optimized (i.e. reordered by some heuristics, e.g. selectivity
	 * estimation)
	 * 
	 * @param pattern
	 * @param qi
	 * @param execCxt
	 * @return QueryIterator
	 */
	@Override
	public QueryIterator execute(BasicPattern pattern, QueryIterator qi,
			ExecutionContext execCxt) {
		Context context = execCxt.getContext();
		Graph graph = execCxt.getActiveGraph();

		// Do this only for in-memory models
		if (graph instanceof GraphMemBase) {
			// This is mainly for test cases (TestEnabled)
			context.set(Constants.isEnabled, true);
			BasicPatternOptimizer optimizer = new BasicPatternOptimizer(
					context, graph, pattern, config);
			BasicPattern optimized = optimizer.optimize();

			/*
			 * Check for consistency between the original and the optimized
			 * pattern. The two patterns are consistent if the optimized pattern
			 * contains every triple contained in the original pattern and the
			 * size of the two patterns is equal.
			 */
			if (!isConsistent(pattern, optimized))
				throw new ARQException(
						"Optimizer returned an inconsistent pattern: "
								+ pattern + " " + optimized);

			// StageGeneratorGeneric gs;

			return QueryIterBlockTriples.create(qi, pattern, execCxt);
		}

		context.set(Constants.isEnabled, false);

		return other.execute(pattern, qi, execCxt);
	}

	private boolean isConsistent(BasicPattern pattern, BasicPattern optimized) {
		List<?> patternL = pattern.getList();
		List<?> optimizedL = optimized.getList();

		if (patternL.containsAll(optimizedL)
				&& optimizedL.containsAll(patternL))
			return true;

		return false;
	}
}

/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development
 * Company, LP All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */