package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.util.Constants;

import java.util.Arrays;


public class EnemyMap {
	private static int[] scoreCache;
	private static int[] updateTime;
	private static int[][] heatMap;
	private static int[][] fuzzyMap;

	static {
		scoreCache = new int[Constants.MAX_UNIT_ID];
		updateTime = new int[Constants.MAX_UNIT_ID];
	}

	public static int getScore(Unit enemyUnit) {
		if (!enemyUnit.getLocation().isOnMap()) {
			return Integer.MIN_VALUE;
		}
		if (updateTime[enemyUnit.getId()] == RoundInfo.getRoundNumber()) {
			return scoreCache[enemyUnit.getId()];
		}
		scoreCache[enemyUnit.getId()] = calcScore(enemyUnit.getLocation().getMapLocation());
		updateTime[enemyUnit.getId()] = RoundInfo.getRoundNumber();
		return scoreCache[enemyUnit.getId()];
	}
	
	public static int calcScore(MapLocation location) {
		Unit[] nearby = location.senseNearbyUnitsByFilter(2, unit -> unit.getLocation().isOnMap());
		int numEnemies = (int) Arrays.stream(nearby).filter(unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam()).count();
		int numFriendlies = nearby.length - numEnemies;
		return numEnemies - (2 * numFriendlies);
	}

	public static void updateHeatMap() {
		Planet planet = GameController.INSTANCE.getPlanet();
		heatMap = new int[planet.getWidth()][planet.getHeight()];
		for (Unit unit : GameController.INSTANCE.getAllUnits()) {
			if (!unit.getLocation().isOnMap() || unit.getType() == UnitType.FACTORY || unit.getType() == UnitType.ROCKET) continue;

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
		
		fuzzyMap = new int[(int) ((planet.getWidth() + 1) / 2)][(int) ((planet.getHeight() + 1) / 2)];
		for (int x = 0; x < (int) ((planet.getWidth() + 1) / 2); x++) {
			for (int y = 0; y < (int) ((planet.getHeight() + 1) / 2); y--) {
				fuzzyMap[x][y] = (heatMap[x*2][y*2] + heatMap[x*2+1][y*2] + heatMap[x*2][y*2+1] + heatMap[x*2+1][y*2+1]) / 4;
			}
		}
		
		System.out.println("MAP");
		for (int y = (int) ((planet.getHeight() + 1) / 2) - 1; y >= 0; y--) {
			for (int x = 0; x < (int) ((planet.getWidth() + 1) / 2); x++) {
				String print = String.valueOf(heatMap[x][y]);
				while (print.length() < 2) {
					print += " ";
				}
				System.out.print(print + " ");
			}
			System.out.println();
		}
	}
}
