/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package stocker.heuristic;

import stocker.probability.Probability;
import stocker.util.Constants;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.util.Context;

/**
 * The heuristics is a wrapper to the probabilistic framework.
 * 
 * @author Markus Stocker
 */

public class ProbabilisticFramework extends HeuristicBasicPattern {
	private Probability probability;

	public ProbabilisticFramework(Context context) {
		probability = (Probability) context.get(Constants.PF);
	}

	/**
	 * This method returns the cost for the triple pattern as the probability
	 * returned by the probabilistic framework.
	 * 
	 * @param triple1
	 * @return double
	 */
	public double getCost(Triple triple1) {
		if (probability == null)
			throw new NullPointerException(
					"The probability framework has not been set to the ARQ context!");

		return probability.getProbability(triple1);
	}

	/**
	 * This method returns the cost for the triple patterns as the probability
	 * returned by the probabilistic framework.
	 * 
	 * @param triple1
	 * @param triple2
	 * @return double
	 */
	public double getCost(Triple triple1, Triple triple2) {
		if (probability == null)
			throw new NullPointerException(
					"The probability framework has not been set to the ARQ context!");

		return probability.getProbability(triple1, triple2);
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