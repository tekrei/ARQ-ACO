/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package stocker.heuristic;

import java.util.HashMap;
import java.util.Map;

import arqaco.utility.SWANTLogger;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.sparql.util.Context;

/**
 * The class is a registry for heuristics
 * 
 * @author Markus Stocker
 */

public class HeuristicsRegistry {
	private Map<String, Heuristic> registry = new HashMap<String, Heuristic>(); // Map<String,
																				// Heuristic>

	public static final String BGP_VARIABLE_COUNTING = "BGP_VARIABLE_COUNTING";
	public static final String BGP_PROBABILISTIC_FRAMEWORK = "BGP_PROBABILISTIC_FRAMEWORK";
	public static final String BGP_PROBABILISTIC_FRAMEWORK_JOIN = "BGP_PROBABILISTIC_FRAMEWORK_JOIN";
	public static final String BGP_GRAPH_STATISTICS_HANDLER = "BGP_GRAPH_STATISTICS_HANDLER";
	public static final String BGP_VARIABLE_COUNTING_UNBOUND = "BGP_VARIABLE_COUNTING_UNBOUND";
	public static final String BGP_OPTIMAL_NO_STATS = "BGP_OPTIMAL_NO_STATS";

	public HeuristicsRegistry() {
	}

	/** The constructor initializes the registry of available heuristics */
	public HeuristicsRegistry(Context context, Graph graph) {
		long start = System.currentTimeMillis();
		add(BGP_VARIABLE_COUNTING, new VariableCounting());
		SWANTLogger.info("VC:" + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		add(BGP_PROBABILISTIC_FRAMEWORK, new ProbabilisticFramework(context));
		SWANTLogger.info("PF:" + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		add(BGP_PROBABILISTIC_FRAMEWORK_JOIN, new ProbabilisticFrameworkJoin(
				context));
		SWANTLogger.info("PFJ:" + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		add(BGP_GRAPH_STATISTICS_HANDLER, new GraphStatisticsHeuristic(graph));
		SWANTLogger.info("GSH:" + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		add(BGP_VARIABLE_COUNTING_UNBOUND, new VariableCountingUnbound());
		SWANTLogger.info("VCU:" + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
		add(BGP_OPTIMAL_NO_STATS, new OptimalNoStats(graph));
		SWANTLogger.info("ONS:" + (System.currentTimeMillis() - start));
		start = System.currentTimeMillis();
	}

	/**
	 * Extract a heuristic instance given it's name, which is used as key for
	 * the registry.
	 * 
	 * @param heuristic
	 * @return Heuristic
	 */
	public Heuristic get(String heuristic) {
		if (registry.containsKey(heuristic)) {
			return (Heuristic) registry.get(heuristic);
		} else {
			SWANTLogger.fine("Heuristic not found in registry: " + heuristic);
		}

		return null;
	}

	/**
	 * Check if a heuristic is registred
	 * 
	 * @param heuristic
	 * @return boolean
	 */
	public boolean isRegistred(String heuristic) {
		if (registry.containsKey(heuristic))
			return true;

		return false;
	}

	/**
	 * Add a heuristic to the registry including the instance of the heuristic
	 * and the corresponding name.
	 * 
	 * @param name
	 * @param heuristic
	 */
	public void add(String name, Heuristic heuristic) {
		registry.put(name, heuristic);
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