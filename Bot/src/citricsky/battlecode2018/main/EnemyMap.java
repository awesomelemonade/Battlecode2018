package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.util.Constants;
import citricsky.battlecode2018.util.Util;


public class EnemyMap {
	private static Planet planet;
	private static int[] scores; // Used for calculating mage attacks
	private static int[][] heatMap; // Used for density of enemies vs friendlies; decides attacking/retreating
	private static final int ROUND_BITMASK = 10;
	private static final int DATA_BITMASK = 22;
	private static final int ROUND_SHIFT = 0;
	private static final int DATA_SHIFT = 12;

	static {
		planet = GameController.INSTANCE.getPlanet();
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

	public static int getHeatMapScore(Vector position) {
		if (Util.outOfBounds(position, heatMap.length, heatMap[0].length)) {
			return Integer.MIN_VALUE;
		}
		if (((heatMap[position.getX()][position.getY()] >>> ROUND_SHIFT) & ROUND_BITMASK) == RoundInfo.getRoundNumber()) {
			return (heatMap[position.getX()][position.getY()] >>> DATA_SHIFT) & DATA_BITMASK;
		}
		heatMap[position.getX()][position.getY()] = ((RoundInfo.getRoundNumber() & ROUND_BITMASK) << ROUND_SHIFT) | 
				((calcHeatMap(position) & DATA_BITMASK) << DATA_SHIFT);
		return (heatMap[position.getX()][position.getY()] >>> DATA_SHIFT) & DATA_BITMASK;
	}
	
	public static int calcHeatMap(Vector position) {
		int score = 0;
		for (Unit unit : GameController.INSTANCE.getAllUnits()) {
			if (unit.getType() == UnitType.FACTORY || unit.getType() == UnitType.ROCKET ||
					unit.getType() == UnitType.WORKER || unit.getType() == UnitType.HEALER ||
					(!unit.getLocation().isOnMap())) continue;
			int distanceSquared = unit.getLocation().getMapLocation().getPosition().getDistanceSquared(position);
			int attackRange = unit.getType().getBaseAttackRange();
			if (unit.getType() == UnitType.KNIGHT) {
				attackRange = 5;
			}
			if (distanceSquared > attackRange) {
				continue;
			}
			if (unit.getType() == UnitType.RANGER && distanceSquared <= unit.getRangerCannotAttackRange() &&
					unit.getTeam() == GameController.INSTANCE.getEnemyTeam()) {
				continue;
			}
			score += unit.getTeam() == GameController.INSTANCE.getTeam() ? 1 : -1;
		}
		return score;
	}
}
