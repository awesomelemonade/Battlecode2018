package citricsky.battlecode2018.util;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
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
				while (allPairs[i][j].getQueueSize() > 0) {
					allPairs[i][j].step();
				}
			}
		}
	}
	public static boolean isPassableTerrain(Vector vector) {
		if (Util.outOfBounds(vector, planet)) {
			return false;
		}
		return planet.getMapLocation(vector).isPassableTerrain();
	}
	public static boolean move(Unit unit, Vector destination) {
		if (!unit.isMoveReady()) {
			return false;
		}
		Vector source = unit.getLocation().getMapLocation().getPosition();
		BFS bfs = allPairs[destination.getX()][destination.getY()];
		Direction candidate = bfs.getDirectionToSource(source.getX(), source.getY());
		if (candidate == null) {
			return false;
		}
		if (unit.canMove(candidate)) {
			unit.move(candidate);
		} else {
			Vector candidateVector = source.add(candidate.getOffsetVector());
			int step = bfs.getStep(candidateVector.getX(), candidateVector.getY());
			for (Direction direction: Direction.COMPASS) {
				if (direction == candidate) {
					continue;
				}
				Vector offset = source.add(direction.getOffsetVector());
				if (bfs.getStep(offset.getX(), offset.getY()) == step) {
					if (unit.canMove(direction)) {
						unit.move(direction);
						return true;
					}
				}
			}
		}
		return false;
	}
}
