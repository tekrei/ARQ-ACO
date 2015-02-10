package arqaco.ai.aco;

/**
 * 
 * @author E. Guzel Kalayci
 * 
 */
public class Ant implements Cloneable {

	public int[] path; // ant path, holds the index of nodes

	public int pathIndex; // path index

	private static final int INITVALUE = -1; // initial value

	private int startPos; // starting position of ant

	public double pathcost; // path cost

	private boolean random;

	public int joinCount;

	/**
	 * @param graphSize size of the graph that ant traverse
	 * @param startPos beginning position of the ant
	 */
	public Ant(int graphSize, int startPos, boolean rndAnt) {
		path = new int[graphSize];
		this.startPos = startPos;
		this.random = rndAnt;
		initPath();
	}

	public int getPosition() {
		return path[pathIndex - 1];
	}

	public void initPath() {
		for (int i = 0; i < path.length; i++) {
			path[i] = INITVALUE;
		}
		pathIndex = 1;
		// everytime start at a random position
		if (random) {
			startPos = AntSystem.getRandomNode();
		}
		path[0] = startPos;
		pathcost = 0.0;
	}

	public void moveTo(int loc) {
		path[pathIndex] = loc;
		pathIndex++;
	}

	public boolean visited(int loc) {
		boolean visited = false;
		for (int i = 0; i < path.length; i++) {
			if (path[i] == loc) {
				visited = true;
				break;
			} else if (path[i] == INITVALUE)
				break; // array sonu
		}
		return visited;
	}

	public static String getPathAsString(int[] _path) {
		String s = "[";
		for (int i = 0; i < _path.length; i++) {
			s += _path[i] + " ";
		}
		return s + "]";
	}

	public String getPathAsString() {
		return getPathAsString(path);
	}

	@Override
	public String toString() {
		return "path:" + getPathAsString() + " cost:" + pathcost
				+ " joinCount:" + joinCount;
	}

	@Override
	public Ant clone() {
		try {
			Ant newAnt = (Ant) super.clone();
			newAnt.path = this.path.clone();
			newAnt.pathcost = this.pathcost;
			return newAnt;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}