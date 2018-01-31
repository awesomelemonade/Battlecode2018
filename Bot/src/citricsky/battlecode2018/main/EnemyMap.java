package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.util.Constants;


public class EnemyMap {
	private static int[] scores; // Used for calculating mage attacks
	private static int[][] heatMap; // Used for density of enemies vs friendlies; decides attacking/retreating
	private static final int ROUND_BITMASK = 10;
	private static final int DATA_BITMASK = 22;
	private static final int ROUND_SHIFT = 0;
	private static final int DATA_SHIFT = 12;

	static {
		Planet planet = GameController.INSTANCE.getPlanet();
		scores = new int[Constants.MAX_UNIT_ID];
		heatMap = new int[planet.getWidth()][planet.getHeight()];
	}

	public static int getScore(Unit enemyUnit) {
		if (!enemyUnit.getLocation().isOnMap()) {
			return Integer.MIN_VALUE;
		}
		if (((scores[enemyUnit.getId()] >>> ROUND_SHIFT) & ROUND_BITMASK) == RoundInfo.getRoundNumber()) {
			return (scores[enemyUnit.getId()] >>> DATA_SHIFT) & DATA_BITMASK;
		}
		scores[enemyUnit.getId()] = ((RoundInfo.getRoundNumber() & ROUND_BITMASK) << ROUND_SHIFT) |
				((calcScore(enemyUnit.getLocation().getMapLocation()) & DATA_BITMASK) << DATA_SHIFT);
		return (scores[enemyUnit.getId()] >>> DATA_SHIFT) & DATA_BITMASK;
	}
	
	public static int calcScore(MapLocation location) {
		int numFriendlies = 0;
		int numEnemies = 0;
		for (Direction direction: Direction.values()) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (offset.hasUnitAtLocation()) {
				if (offset.getUnit().getTeam() == GameController.INSTANCE.getTeam()) {
					numFriendlies++;
				} else {
					numEnemies++;
				}
			}
		}
		return numEnemies - (2 * numFriendlies);
	}

	public static void updateHeatMap() {
		for (int i = 0; i < heatMap.length; ++i) {
			for (int j = 0; j < heatMap[0].length; ++j) {
				heatMap[i][j] = 0;
			}
		}
		for (Unit unit : GameController.INSTANCE.getAllUnits()) {
			if (unit.getType() == UnitType.FACTORY || unit.getType() == UnitType.ROCKET ||
					unit.getType() == UnitType.WORKER || (!unit.getLocation().isOnMap())) continue;

			int mult = unit.getTeam() == GameController.INSTANCE.getTeam() ? 1 : -1;

			MapLocation loc = unit.getLocation().getMapLocation();
			Vector pos = loc.getPosition();
			int cX = pos.getX();
			int cY = pos.getY();

			heatMap[cX][cY] += 3*mult;

			for (MapLocation targetLoc : loc.getAllMapLocationsWithin(unit.getVisionRange())) {
				Vector targetPos = targetLoc.getPosition();
				heatMap[targetPos.getX()][targetPos.getY()] += 1*mult;
			}
			if (unit.getType() == UnitType.RANGER) {
				for (MapLocation targetLoc : loc.getAllMapLocationsWithin(unit.getRangerCannotAttackRange())) {
					Vector targetPos = targetLoc.getPosition();
					heatMap[targetPos.getX()][targetPos.getY()] -= 1*mult;
				}
			}
		}
	}
}
