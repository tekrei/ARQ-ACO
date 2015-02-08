/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package stocker.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import arqaco.utility.SWANTLogger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.BasicPattern;

/**
 * The class implements a connected graph, i.e. a component of the
 * BasicPatternGraph, and the abstract optimization logic based on node and edge
 * costs.
 * 
 * @author Markus Stocker
 */

public class ConnectedGraph {
	// Prime number generator, used for nodes OIDs
	private PrimeNumberGen prime = new PrimeNumberGen();
	/*
	 * The object ID of nodes are prime numbers. The object ID of edges is
	 * calculated as multiplication of the OIDs for the corresponding nodes. The
	 * nodes OID starts with 1 in order that the first allocated node has OID 2,
	 * the first prime number. Note that the selection of prime numbers is used
	 * because the multiplication of prime numbers is assured to be unique. We
	 * exploit this property in order to uniquely identify the edge given the
	 * corresponding nodes.
	 */
	private int OID = 1;
	// The first prime number is ...
	private int MIN_OID = prime.first();
	// The set of optimized GraphNode objects
	private Set<GraphNode> optGraphNodeList = new LinkedHashSet<GraphNode>(); // Set<GraphNode>
	// The set of optimized GraphEdge objects
	private Set<GraphEdge> optGraphEdgeList = new LinkedHashSet<GraphEdge>(); // Set<GraphEdge>
	// The list of nodes for the ConnectedGraph, uses the natural sort of OIDs
	// for TreeMaps
	private Map<Integer, GraphNode> nodes = new TreeMap<Integer, GraphNode>(); // Map<Integer,
																				// GraphNode>
	// The list of edges for the ConnectedGraph, uses the natural sort of OIDs
	// for TreeMaps
	private Map<Integer, GraphEdge> edges = new TreeMap<Integer, GraphEdge>(); // Map<Integer,
																				// GraphEdge>
	// This inverted index maps a triple hash code of a node to the
	// corresponding OID
	private Map<Triple, Integer> nodesInvertedIndex = new HashMap<Triple, Integer>(); // Map<Triple,
																						// Integer>

	/**
	 * Optimize a connected graph according to our BasicGraphPattern
	 * optimization approach.
	 * 
	 * @return BasicPattern
	 */
	public BasicPattern optimize() {
		/*
		 * This method implements the logic required to optimize a component of
		 * a BasicGraphPattern according to the weight of the nodes and edges.
		 * Please note that the corresponding weights are stored directly in the
		 * GraphNode and GraphEdge objects.
		 */
		BasicPattern pattern = new BasicPattern();

		/*
		 * Get the optimized ordered set of nodes, where the ordering is defined
		 * first by the total cost for the edges and for each each by the cost
		 * for the corresponding nodes.
		 */
		optGraphNodeList = getOptimizedNodeList();

		// Given P, get the triples of the nodes and add them to a BasicPattern
		for (Iterator<GraphNode> iter = optGraphNodeList.iterator(); iter
				.hasNext();) {
			GraphNode node = (GraphNode) iter.next();
			pattern.add(node.triple());
		}

		return pattern;
	}

	/**
	 * The method allows to allocate a new node for the graph. The node stores
	 * the corresponding triple pattern and the weight calculated by the
	 * selected heuristic.
	 * 
	 * @param triple
	 * @param weight
	 * @return GraphNode
	 */
	public GraphNode createNode(Triple triple, double weight) {
		/*
		 * Please note that nodes are internally identified using object IDs.
		 * Each ConnectedGraph manages its own OIDs, which are prime numbers
		 * generated each time a new node is created. Note that in order to
		 * create an edge, first the corresponding nodes has to be allocated.
		 */
		// Find the next prime number
		int oid = nextOID();
		// Create the corresponding graph node
		GraphNode node = new GraphNode(oid, triple, weight);
		// Store the triple hash code with the corresponding OID to the inverted
		// index
		nodesInvertedIndex.put(triple, new Integer(oid));

		nodes.put(new Integer(oid), node);

		return node;
	}

	/**
	 * The method allows to allocate a new edge for the graph. An edge is
	 * described by its corresponding nodes and the weight calculated by the
	 * selected heuristic. Please note that to allocate a new edge, it is
	 * required that the corresponding nodes are previously allocated.
	 * 
	 * @param node1
	 * @param node2
	 * @param weight
	 * @return GraphEdge
	 */
	public GraphEdge createEdge(GraphNode node1, GraphNode node2, double weight) {
		/*
		 * Create the OID, which is the negative multiplication of the OIDs for
		 * the corresponding nodes. Thus, while nodes have increasing OIDs,
		 * edges have negative decreasing OIDs. This is to ensure that there are
		 * no collisions of OIDs between nodes and edges. Although the list of
		 * nodes is kept separated from the list of edges, and, thus, duplicated
		 * OIDs could be used, we prefer to keep OIDs unique for a
		 * ConnectedGraph, since it is the definition of OID.
		 */
		int oid = node1.oid() * node2.oid();
		// This formula should return a unique oid for edges too, but I couldn't
		// prove it
		// Integer oid = - (int)((Math.pow(node1.oid() + node2.oid() - 1, 2) +
		// node1.oid() - node2.oid() + 1) / 2) ;
		// Create the corresponding graph edge
		GraphEdge edge = new GraphEdge(oid, node1, node2, weight);

		edges.put(new Integer(oid), edge);

		return edge;
	}

	/**
	 * The method allows a retrieval of a GraphNode, a specific node of the
	 * ConnectedGraph by OID lookup.
	 * 
	 * @param oid
	 * @return GraphNode
	 */
	public GraphNode getNodeByOID(Integer oid) {
		if (!nodes.containsKey(oid))
			SWANTLogger.severe("GraphNode not found: " + oid);

		return (GraphNode) nodes.get(oid);
	}

	/**
	 * The method allows a retrieval of a GraphEdge, a specific edge of the
	 * ConnectedGraph by OID lookup. Please note that the OID of an edge is
	 * modeled as the multiplication of the OIDs of the corresponding nodes.
	 * 
	 * @param oid
	 * @return GraphEdge
	 */
	public GraphEdge getEdgeByOID(Integer oid) {
		if (!edges.containsKey(oid))
			SWANTLogger.severe("GraphEdge not found: " + oid);

		return (GraphEdge) edges.get(oid);
	}

	/**
	 * The method allows a retrieval of a GraphNode, a specific node of the
	 * ConnectedGraph by the triple which corresponds to the node. Internally,
	 * the method uses an inverted hash index which maps triple hash codes to
	 * the corresponding OID to efficiently retrieve the OID given a triple.
	 * 
	 * @param triple
	 * @return GraphNode
	 */
	public GraphNode getNodeByTriple(Triple triple) {
		// Retrieve the OID of the triple from the inverted index
		Integer oid = (Integer) nodesInvertedIndex.get(triple);

		if (!nodes.containsKey(oid))
			SWANTLogger.severe("GraphNode not found: " + oid);

		return (GraphNode) nodes.get(oid);
	}

	/**
	 * The method allows a retrieval of a GraphEdge, a specific edge of the
	 * ConnectedGraph by given the two triples which correspond to the nodes of
	 * the edge.
	 * 
	 * @param triple1
	 * @param triple2
	 * @return GraphEdge
	 */
	public GraphEdge getEdgeByTriples(Triple triple1, Triple triple2) {
		// Retrieve the GraphNodes which correspond to both the triple1 and
		// triple2
		GraphNode node1 = getNodeByTriple(triple1);
		GraphNode node2 = getNodeByTriple(triple2);

		// Get the OID for the edge
		int oid = node1.oid() * node2.oid();
		// Integer oid = - (int)((Math.pow(node1.oid() + node2.oid() - 1, 2) +
		// node1.oid() - node2.oid() + 1) / 2) ;

		if (!edges.containsKey(new Integer(oid)))
			SWANTLogger.severe("GraphEdge not found: " + oid);

		return (GraphEdge) edges.get(new Integer(oid));
	}

	/**
	 * The method returns the List<GraphNode> of the nodes defined for the
	 * ConnectedGraph. Note that the list is ordered by the OID, i.e. by the
	 * sequence the nodes were added to the connected graph. This is for
	 * convenience only (i.e. for testing purposes). In fact, the set of nodes
	 * of a graph is defined as unordered.
	 * 
	 * @return List<GraphNode>
	 */
	public List<GraphNode> getNodes() {
		List<GraphNode> list = new ArrayList<GraphNode>(); // List<GraphNode>

		for (Iterator<GraphNode> iter = nodes.values().iterator(); iter
				.hasNext();) {
			list.add((GraphNode) iter.next());
		}

		return list;
	}

	/**
	 * The method returns the List<GraphEdge> of the edges defined for the
	 * ConnectedGraph. Node that the list is ordered by the OID, i.e. by the
	 * sequence the edges were added to the connected graph. This is for
	 * conveniece only. In fact, the set of edges of a graph is defined as
	 * unordered.
	 * 
	 * @return List<GraphEdge>
	 */
	public List<GraphEdge> getEdges() {
		List<GraphEdge> list = new ArrayList<GraphEdge>(); // List<GraphEdge>

		for (Iterator<GraphEdge> iter = edges.values().iterator(); iter
				.hasNext();) {
			list.add((GraphEdge) iter.next());
		}

		return list;
	}

	/**
	 * Return the optimized list of GraphNode objects, i.e. the Query Execution
	 * Plan. The method returns null if the ConnectedGraph is not yet optimized,
	 * i.e. the method optimize() is not yet executed.
	 * 
	 * @return Set<GraphNode>
	 */
	public Set<GraphNode> getOptGraphNodeList() {
		// Set<GraphNode>
		return optGraphNodeList;
	}

	/**
	 * Return the optimized list of GraphEdge objects, The method returns null
	 * if the ConnectedGraph is not yet optimized, i.e. the method optimize() is
	 * not yet executed.
	 * 
	 * @return Set<GraphEdge>
	 */
	public Set<GraphEdge> getOptGraphEdgeList() {
		// Set<GraphEdge>
		return optGraphEdgeList;
	}

	/*
	 * Prime number generator for node ODIs
	 */
	private int nextOID() {
		OID = prime.next();

		return OID;
	}

	/*
	 * The method returns the ordered set of nodes which reflects the most
	 * promising QEP, i.e. the nodes are reorder first according to the total
	 * cost of edges, and then according to the cost of the nodes.
	 */
	private Set<GraphNode> getOptimizedNodeList() {
		// Set<GraphNode>
		// If the ConnectedGraph contains no edge, i.e. only one node
		if (edges.size() == 0 && nodes.size() == 1)
			return optimizeSingleNode();

		return optimizeMultipleNodes();
	}

	// Not much to optimize, just return the node in a list
	private Set<GraphNode> optimizeSingleNode() {
		// Loglayici.fine("Optimizing ConnectedGraph with one node, no edge") ;

		// Set<GraphNode>
		// The ordered set of nodes which reflect the QEP
		Set<GraphNode> qep = new LinkedHashSet<GraphNode>(); // Set<GraphNode>

		qep.add(nodes.get(new Integer(MIN_OID)));

		if (qep.size() != nodes.size())
			SWANTLogger
					.severe("The optimized set of nodes does not contain each node");

		return qep;
	}

	private Set<GraphNode> optimizeMultipleNodes() {
		GraphEdge edge = null;
		List<GraphNode> nodes = null; // List<GraphNode>
		// Loglayici.fine("Optimizing ConnectedGraph with " + this.nodes.size()
		// + " nodes and " + this.edges.size() + " edges");

		// The ordered set of nodes which reflect the QEP
		Set<GraphNode> nodeQEP = new LinkedHashSet<GraphNode>(); // Set<GraphNode>
		// Init the set of edges ordered by total cost
		List<GraphEdge> edges = new ArrayList<GraphEdge>(); // List<GraphEdge>

		// Create a local copy of the list of edges
		edges.addAll(this.edges.values());
		// ... and sort it according to the EdgeComparator
		Collections.sort(edges, new EdgeComparator());
		// This is required for the ARQo.explain() method
		optGraphEdgeList.addAll(edges);
		// Mark the edge as considered by removing it from the list
		edge = (GraphEdge) edges.remove(0);
		// Get the nodes of the considered edge
		nodes = edge.nodes();
		// Sort the nodes by increasing weight
		Collections.sort(nodes, new NodeComparator());
		// Add the nodes to the QEP
		nodeQEP.addAll(nodes);

		// While the QEP does not contain all nodes
		while (nodeQEP.size() < this.nodes.size()) {
			edge = getNextEdge(nodeQEP, edges);
			edges.remove(edge);
			nodes = edge.nodes();
			Collections.sort(nodes, new NodeComparator());

			// Check if the node is in the list
			for (Iterator<?> iter = nodes.iterator(); iter.hasNext();) {
				GraphNode node = (GraphNode) iter.next();

				if (!nodeQEP.contains(node))
					nodeQEP.add(node);
			}
		}

		return nodeQEP;
	}

	private GraphEdge getNextEdge(Set<GraphNode> qep, List<GraphEdge> edges) {
		// qep = Set<GraphNode>
		// edges = List<GraphEdge>
		// Find the next edge with a node already considered
		for (Iterator<GraphEdge> iter = edges.iterator(); iter.hasNext();) {
			GraphEdge edge = (GraphEdge) iter.next();
			List<?> nodes = edge.nodes(); // List<GraphNode>

			for (Iterator<?> it = nodes.iterator(); it.hasNext();) {
				GraphNode node = (GraphNode) it.next();

				if (qep.contains(node))
					return edge;
			}
		}

		throw new NullPointerException(
				"FATAL. No edge found which connects to previous nodes in the QEP");
	}

	// The class implements a comparator to sort edges according to the total
	// cost
	class EdgeComparator implements Comparator<Object> {
		public int compare(Object edge1, Object edge2) {
			if (!(edge1 instanceof GraphEdge))
				throw new ClassCastException();
			if (!(edge2 instanceof GraphEdge))
				throw new ClassCastException();

			if (((GraphEdge) edge1).weight() < ((GraphEdge) edge2).weight())
				return -1;
			if (((GraphEdge) edge1).weight() > ((GraphEdge) edge2).weight())
				return 1;

			return 0;
		}
	}

	// The class implements a comparator to sort nodes according to their cost
	class NodeComparator implements Comparator<Object> {
		public int compare(Object node1, Object node2) {
			if (!(node1 instanceof GraphNode))
				throw new ClassCastException();
			if (!(node2 instanceof GraphNode))
				throw new ClassCastException();

			if (((GraphNode) node1).weight() < ((GraphNode) node2).weight())
				return -1;
			if (((GraphNode) node1).weight() > ((GraphNode) node2).weight())
				return 1;

			return 0;
		}
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