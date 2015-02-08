package arqaco.ai;

import arqaco.ai.ds.TPV;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public interface Optimization {
	public int[] executeAlgorithm(TPV parts);

	public int[] executeAlgorithm(TPV parts, int start);

	public int getStart();

	public int getEnd();
}