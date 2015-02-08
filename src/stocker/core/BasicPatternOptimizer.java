/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package stocker.core;

import stocker.heuristic.HeuristicBasicPattern;
import stocker.util.Config;
import arqaco.utility.SWANTLogger;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.util.Context;

/**
 * Implementation of basic graph pattern optimizer.
 * 
 * @author Markus Stocker
 */

public class BasicPatternOptimizer extends OptimizerBase {
	private BasicPattern pattern = null;
	private BasicPatternGraph basicPatternGraph = null;
	private HeuristicBasicPattern heuristic = null;

	/**
	 * Initialize the BGP optimizer.
	 * 
	 * @param context
	 * @param graph
	 * @param pattern
	 * @param config
	 */
	public BasicPatternOptimizer(Context context, Graph graph,
			BasicPattern pattern, Config config) {
		super(context, graph);
		long start = System.currentTimeMillis();
		this.pattern = pattern;

		if (config == null)
			this.heuristic = broker.getBasicPatternHeuristic();
		else
			this.heuristic = broker.getBasicPatternHeuristic(config
					.getBasicPatternHeuristic());
		SWANTLogger.info("LOADED (" + (System.currentTimeMillis() - start)
				+ ") " + this.heuristic);
	}

	/**
	 * Return the initialized basic pattern heuristic, which is selected by the
	 * broker during initialization of the BasicPatternOptimizer. This method
	 * was added for testing purposes (see TestConfig).
	 * 
	 * @return HeuristicBasicPattern
	 */
	public HeuristicBasicPattern getHeuristicBasicPattern() {
		return heuristic;
	}

	/**
	 * Given a BasicPattern, the method abstracts the BasicPattern as a
	 * BasicPatternGraph and inits the optimization program.
	 * 
	 * @return BasicPattern
	 */
	public BasicPattern optimize() {
		// Create an abstracted graph representation of the BasicPattern
		basicPatternGraph = new BasicPatternGraph(pattern, heuristic);
		// Optimize the abstracted graph and return the optimized BasicPattern
		return basicPatternGraph.optimize();
	}

	/**
	 * Return the BasicPatternGraph, used mainly to explain optimizations.
	 * 
	 * @return BasicPatternGraph
	 */
	public BasicPatternGraph getBasicPatternGraph() {
		// If the method is executed without previously executing optimize(),
		// optimize first
		if (basicPatternGraph == null)
			optimize();

		return basicPatternGraph;
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