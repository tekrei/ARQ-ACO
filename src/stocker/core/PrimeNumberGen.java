/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package stocker.core;

/**
 * The class is a prime number generator.
 * 
 * @author Markus Stocker
 */

public class PrimeNumberGen {
	private static final int start = 1;
	private static final int first = 2;
	private int prime = start;

	/**
	 * Return the first prime number, i.e. 2
	 * 
	 * @return int
	 */
	public int first() {
		return first;
	}

	/**
	 * Reset the generator to start at the first prime
	 */
	public void reset() {
		prime = start;
	}

	/**
	 * Return the next prime number
	 * 
	 * @return int
	 */
	public int next() {
		prime++;

		while (!isPrime(prime))
			prime++;

		return prime;
	}

	/**
	 * Given a natural number return the next prime number
	 * 
	 * @param start
	 * @return int
	 */
	public int next(int start) {
		prime = start;

		return next();
	}

	// Check if a natural number is a prime number
	private boolean isPrime(int next) {
		for (int i = 2; i <= (next / 2); i++) {
			if (i * (next / i) == next)
				return false;
		}

		return true;
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