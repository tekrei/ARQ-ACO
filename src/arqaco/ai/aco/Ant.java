package arqaco.ai.aco;

/**
 * 
 * @author kalayci
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
	 * Constructor.
	 * 
	 * @param graphSize
	 *            Karincanin dolasacagi graphin buyuklugu
	 * @param startPos
	 *            Karincanin graphi dolamaya baslayacagi nokta indexi
	 */
	public Ant(int graphSize, int startPos, boolean rndAnt) {
		path = new int[graphSize];
		this.startPos = startPos;
		this.random = rndAnt;
		initPath();
	}

	/**
	 * Method getPosition. Karincanin hangi yerde olduunu dndrr
	 * 
	 * @return int
	 */
	public int getPosition() {
		return path[pathIndex - 1];
	}

	/**
	 * Method initPath. Karincayi yolun basina alir ve path array'ini sifirlar
	 */
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

	/**
	 * Method moveTo. gidilen yeri path array'ine ekleyip index'i arttrr
	 * 
	 * @param loc
	 *            gidilecek yerin index bilgisi
	 */
	public void moveTo(int loc) {
		path[pathIndex] = loc;
		pathIndex++;
	}

	/**
	 * Method visited. Eger loc ile parametre olarak geirilen yer daha nce
	 * ziyaret edilmi ise true aksi halde false dndrrlr
	 * 
	 * @param loc
	 * @return boolean
	 */
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