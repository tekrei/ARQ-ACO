package arqaco.ds;

import java.util.ArrayList;

/**
 * 
 * @author kalayci
 * 
 */
public class QueryResult {
	public String type;
	public ArrayList<String> results;
	public String query;
	// public String resultAsString;
	public long time;
	public int resultCount;

	@Override
	public String toString() {
		return type + "-" + resultCount + "-" + time;
	}
}
