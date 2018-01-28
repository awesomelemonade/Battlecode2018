package citricsky.battlecode2018.util;

import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Vector;

public class AllPairs {
	private static Planet planet;
	private static BFS[][] allPairs;
	public static void init(Planet planet) {
		AllPairs.planet = planet;
		AllPairs.allPairs = new BFS[planet.getWidth()][planet.getHeight()];
		for (int i = 0; i < planet.getWidth(); ++i) {
			for (int j = 0; j < planet.getHeight(); ++j) {
				allPairs[i][j] = new BFS(planet.getWidth(), planet.getHeight(), AllPairs::isPassableTerrain, new Vector(i, j));
			}
		}
	}
	public static boolean isPassableTerrain(Vector vector) {
		if (Util.outOfBounds(vector, planet)) {
			return false;
		}
		return planet.getMapLocation(vector).isPassableTerrain();
	}
}
