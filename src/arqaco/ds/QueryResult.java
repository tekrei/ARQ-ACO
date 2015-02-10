package arqaco.ds;

import java.util.ArrayList;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class QueryResult {
	public String type;
	public ArrayList<String> results;
	public String query;
	public long time;
	public int resultCount;

	@Override
	public String toString() {
		return type + "-" + resultCount + "-" + time;
	}
}
