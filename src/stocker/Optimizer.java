/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package stocker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import stocker.core.BasicPatternGraph;
import stocker.core.BasicPatternOptimizer;
import stocker.core.BasicPatternVisitor;
import stocker.core.ConnectedGraph;
import stocker.core.GraphEdge;
import stocker.core.GraphNode;
import stocker.probability.Probability;
import stocker.probability.ProbabilityFactory;
import stocker.util.Config;
import stocker.util.Constants;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpWalker;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.engine.main.StageBuilder;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.util.Context;

/**
 * The class provides static methods for handling with the optimizer
 * 
 * @author Markus Stocker
 */

public class Optimizer {
	/**
	 * Enable the default ARQ optimizer, no special configuration and globally
	 * enabled in the ARQ context
	 */
	public static void enable() {
		enable(ARQ.getContext(), new Config());
	}

	/**
	 * Enable the ARQ optimizer using the specified configuration and globally
	 * enabled in the ARQ context
	 * 
	 * @param config
	 */
	public static void enable(Config config) {
		enable(ARQ.getContext(), config);
	}

	/**
	 * Enable the ARQ optimizer with the specialized RDF index and globally
	 * enabled in the ARQ context
	 * 
	 * @param data
	 * @param index
	 */
	public static void enable(Model data, Model index) {
		enable(ARQ.getContext(), data, index, null);
	}

	/**
	 * Enable the ARQ optimizer with the specialized RDF index using the
	 * specified configuration options and globally enable the optimizer in the
	 * ARQ context
	 * 
	 * @param data
	 * @param index
	 * @param config
	 */
	public static void enable(Model data, Model index, Config config) {
		enable(ARQ.getContext(), data, index, config);
	}

	/**
	 * Enable the ARQ optimizer with the specialized RDF index using the
	 * specified configuration options and enabled in the context provided (may
	 * be a local context)
	 * 
	 * @param context
	 * @param data
	 * @param index
	 * @param config
	 */
	public static void enable(Context context, Model data, Model index,
			Config config) {
		Probability probability = ProbabilityFactory.loadDefaultModel(data,
				index, config);
		context.set(Constants.PF, probability);
		enable(context, config);
	}

	/**
	 * Enable the ARQ optimizer using the specified configuration options
	 * enabled in the context provided
	 * 
	 * @param context
	 * @param config
	 */
	public static void enable(Context context, Config config) {
		/*
		 * Note that the StageGenOptimizedBasicPattern has to set the flag true
		 * in order to check if the optimizer is REALLY executed. The flag is
		 * mainly for testing purpose (TestEnabled)
		 */
		context.set(Constants.isEnabled, false);
		// context.set(ARQ.stageGenerator, new StageGenPropertyFunction(
		// new StageGenOptimizedBasicPattern(
		// StageBuilder.getGenerator(),
		// config))) ;
		context.set(ARQ.stageGenerator, new StageGenOptimizedBasicPattern(
				StageBuilder.getGenerator(), config));

	}

	/**
	 * Disable the ARQ optimizer and reset to the context provided
	 * 
	 * @param context
	 */
	public static void disable(Context context) {
		context.set(Constants.isEnabled, false);
		context.set(ARQ.stageGenerator, StageBuilder.standardGenerator());
	}

	/** Disable the ARQ optimizer and reset using the ARQ context */
	public static void disable() {
		disable(ARQ.getContext());
	}

	/**
	 * Create and return the Jena model of the specialized RDF index required
	 * for the probabilistic index model. The full index is constructed.
	 * 
	 * @param data
	 * @return Model
	 */
	public static Model index(Model data) {
		return index(data, null);
	}

	/**
	 * Create and return the Jena model of the specialized RDF index required
	 * for the probabilistic index model. The method takes a configuration to
	 * specify user specific options.
	 * 
	 * @param data
	 * @param config
	 * @return Model
	 */
	public static Model index(Model data, Config config) {
		return ProbabilityFactory.createIndex(data, config);
	}

	/**
	 * Create and return the Jena model of the specialized RDF index required
	 * for the probabilistic index model.
	 * 
	 * @param data
	 * @param config
	 * @return Model
	 */
	public static Model index(Graph data, Config config) {
		return ProbabilityFactory.createIndex(data, config);
	}

	/**
	 * Explain the optimization performed on a query. The returned string can be
	 * used for further processing, for example to print it out on console or in
	 * a log file.
	 * 
	 * @param query
	 * @return String
	 */
	public static String explain(Query query) {
		return explain(ARQ.getContext(), null, query, null);
	}

	/**
	 * Explain the optimization performed on a query. For some heuristics the
	 * model is required (e.g. Jena graph statistics handler)
	 * 
	 * @param model
	 * @param query
	 * @return String
	 */
	public static String explain(Model model, Query query) {
		return explain(ARQ.getContext(), model, query, null);
	}

	/**
	 * Explain the optimization performed on a query using a specific
	 * configuration. For some heuristics the model is required (e.g. Jena graph
	 * statistics handler)
	 * 
	 * @param model
	 * @param query
	 * @param config
	 * @return String
	 */
	public static String explain(Model model, Query query, Config config) {
		return explain(ARQ.getContext(), model, query, config);
	}

	/**
	 * Explain the optimization performed on a query. This method allows
	 * settings a specific context. The model and confic parameter can be null.
	 * 
	 * @param context
	 * @param model
	 * @param query
	 * @param config
	 * @return String
	 */
	public static String explain(Context context, Model model, Query query,
			Config config) {
		Graph graph = null;
		String left = "| ";
		String sep = " | ";
		StringBuffer out = new StringBuffer();
		List<List<?>> rowsTable1 = new ArrayList<List<?>>(); // List<List>
		List<List<?>> rowsTable2 = new ArrayList<List<?>>(); // List<List>
		PrefixMapping prefix = query.getPrefixMapping();
		BasicPatternVisitor visitor = new BasicPatternVisitor();
		Element el = query.getQueryPattern();
		Op op = Algebra.compile(el);
		OpWalker.walk(op, visitor);

		if (model != null)
			graph = model.getGraph();

		// The list of BGP defined in the query
		List<?> patterns = visitor.getPatterns(); // List<BasicPattern>

		// Step through the BGP patterns defined in the query
		for (Iterator<?> iter = patterns.iterator(); iter.hasNext();) {
			BasicPattern pattern = (BasicPattern) iter.next();
			// Optimizer the BGP
			BasicPatternOptimizer optimizer = new BasicPatternOptimizer(
					context, graph, pattern, config);
			BasicPatternGraph basicPatternGraph = optimizer
					.getBasicPatternGraph();
			basicPatternGraph.optimize();
			// Return the BGP graph components
			List<?> components = basicPatternGraph.getComponents(); // List<ConnectedGraph>

			// Step through the BGP graph components
			for (Iterator<?> it = components.iterator(); it.hasNext();) {
				ConnectedGraph component = (ConnectedGraph) it.next();
				// Get the optimized list of GraphNodes
				Set<?> nodes = component.getOptGraphNodeList(); // Set<GraphNode>
				Set<?> edges = component.getOptGraphEdgeList(); // Set<GraphEdge>

				for (Iterator<?> i = nodes.iterator(); i.hasNext();) {
					GraphNode node = (GraphNode) i.next();
					Triple triple = node.triple();
					double weight = node.weight();
					rowsTable1.add(ExplainFormatter.formatCols(prefix, triple,
							weight));
				}

				// Queries with only one triple pattern don't have edges!
				if (edges != null && edges.size() > 0) {
					for (Iterator<?> i = edges.iterator(); i.hasNext();) {
						GraphEdge edge = (GraphEdge) i.next();
						Triple triple1 = edge.node1().triple();
						Triple triple2 = edge.node2().triple();
						double weight = edge.weight();
						rowsTable2.add(ExplainFormatter.formatCols(prefix,
								triple1, triple2, weight));
					}
				}
			}
		}

		ExplainFormatter.printTable(out, new String[] { "S", "P", "O", "C" },
				rowsTable1, left, sep);

		out.append("\n");

		// The edges table might be empty (if the query has only one triple
		// pattern, i.e. the graph no edges
		if (rowsTable2.size() > 0)
			ExplainFormatter
					.printTable(out, new String[] { "TP-1", "TP-2", "C" },
							rowsTable2, left, sep);

		return out.toString();
	}

	/** The root package name for ARQo */
	public static final String PATH = "com.hp.hpl.jena.sparql.engine.optimizer";

	/** The product name */
	public static final String NAME = "ARQ-Optimizer";

	/** The product acronym */
	public static final String ACRONYM = "ARQo";

	/** The ARQo web site : see also http://jena.sourceforge.net */
	public static final String WEBSITE = "http://jena.sourceforge.net/ARQ/bgp-optimization.html";

	/*
	 * The full name of the current ARQo version, required to flag the index
	 * ontology with a version number!
	 */
	public static final String VERSION = "0.2";

	/* The major version number for this release of ARQo (ie '2' for ARQo 2.0) */
	// public static final String MAJOR_VERSION = "0";

	/* The minor version number for this release of ARQo (ie '0' for ARQo 2.0) */
	// public static final String MINOR_VERSION = "0";

	/*
	 * The version status for this release of ARQo (eg '-beta1' or the empty
	 * string)
	 */
	// public static final String VERSION_STATUS = "-alpha-1";

	/* The date and time at which this release was built */
	// public static final String BUILD_DATE = "2007-07-02 13:50 +0000";
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