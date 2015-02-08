/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package stocker.heuristic;

import java.util.Iterator;
import java.util.List;

import stocker.core.BasicPatternJoin;

import com.hp.hpl.jena.graph.Triple;

/**
 * This heuristic is very similar to the variable counting heuristic, though it
 * gives weight 1.0 to bound predicate joins. The idea is to avoid that patterns
 * like ?x rdf:type ub:GraduateStudent . ?y rdf:type ub:Department are
 * considered to be good during optimization. By weighting them badly the
 * optimizer considers other joins better choices.
 * 
 * @author Markus Stocker
 */

public class VariableCountingUnbound extends HeuristicBasicPattern {

	/**
	 * This method returns the cost for the triple based on the number and type
	 * of variables defined in the triple.
	 * 
	 * @param triple1
	 * @return Double
	 */
	public double getCost(Triple triple1) {
		double cost = 1d;
		int MAX_COST = 8;

		if (triple1.getSubject().isVariable())
			cost += 4;

		if (triple1.getPredicate().isVariable())
			cost += 1;

		if (triple1.getObject().isVariable())
			cost += 2;

		return cost / MAX_COST;
	}

	/**
	 * The method estimates the execution cost of joined triple patterns based
	 * on simple heuristics. Note that the bound predicate-predicate join is
	 * explicitely weighted 1.0 to avoid that the optimizer choses certain
	 * patterns as "good" optimizations.
	 * 
	 * @param triple1
	 * @param triple2
	 * @return Double
	 */
	public double getCost(Triple triple1, Triple triple2) {
		if (!BasicPatternJoin.isJoined(triple1, triple2)) {
			return Double.MAX_VALUE;
		}

		double cost = 32d;
		int MAX_COST = 32;
		List<?> joins = BasicPatternJoin.specificTypes(triple1, triple2); // List<String>

		for (Iterator<?> iter = joins.iterator(); iter.hasNext();) {
			String type = (String) iter.next();

			// Weight bound PP joins with 1.0, always
			if (type.equals(BasicPatternJoin.bPP))
				return cost / MAX_COST;

			if (type.equals(BasicPatternJoin.uSS))
				cost -= 2;
			else if (type.equals(BasicPatternJoin.uSP))
				cost -= 3;
			else if (type.equals(BasicPatternJoin.uSO))
				cost -= 1;
			else if (type.equals(BasicPatternJoin.uPS))
				cost -= 3;
			else if (type.equals(BasicPatternJoin.uPP))
				cost -= 3;
			else if (type.equals(BasicPatternJoin.uPO))
				cost -= 3;
			else if (type.equals(BasicPatternJoin.uOS))
				cost -= 1;
			else if (type.equals(BasicPatternJoin.uOP))
				cost -= 3;
			else if (type.equals(BasicPatternJoin.uOO))
				cost -= 1;
			else if (type.equals(BasicPatternJoin.bSS))
				cost -= 2 * 2;
			else if (type.equals(BasicPatternJoin.bSP))
				cost -= 3 * 2;
			else if (type.equals(BasicPatternJoin.bSO))
				cost -= 1 * 2;
			else if (type.equals(BasicPatternJoin.bPS))
				cost -= 3 * 2;
			else if (type.equals(BasicPatternJoin.bPO))
				cost -= 3 * 2;
			else if (type.equals(BasicPatternJoin.bOS))
				cost -= 1 * 2;
			else if (type.equals(BasicPatternJoin.bOP))
				cost -= 3 * 2;
			else if (type.equals(BasicPatternJoin.bOO))
				cost -= 1 * 2;
		}

		return cost / MAX_COST * getCost(triple1) * getCost(triple2);
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