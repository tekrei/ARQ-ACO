package arqaco.ds;

/**
 * Class which stores the query information read from *.qr files
 * 
 * \* @author E. Guzel Kalayci
 * 
 */
public class QueryInfo {
	public QueryInfo(String text, int rc) {
		this.query = text;
		this.resultCount = rc;
	}

	public QueryInfo() {
	}

	public String query;
	public int resultCount;
}
