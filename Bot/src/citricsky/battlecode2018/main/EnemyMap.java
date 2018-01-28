package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.util.Constants;

import java.util.*;

public class EnemyMap {
	private static int[] scoreCache;
	private static int[] updateTime;

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
}
