package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;

import java.util.*;

public class EnemyMap {
	private static int lastRound = -1;
	private static Map<MapLocation, Integer> scoreCache = new HashMap<>();
	private static Unit[] enemyUnits;

	public static void update() {
		scoreCache.clear();
		enemyUnits = updateEnemies();
	}

	private static Unit[] updateEnemies() {
		Unit[] enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam() && unit.getLocation().isOnMap());

		Arrays.sort(enemyUnits, (unit1, unit2) -> Integer.compare(getScore(unit2), getScore(unit1))); // Purposefully flipped, to sort largest -> smallest

		lastRound = (int) GameController.INSTANCE.getRoundNumber();
		return enemyUnits;
	}

	public static int getScore(Unit enemyUnit) {
		MapLocation enemyLoc = enemyUnit.getLocation().getMapLocation();
		if (scoreCache.containsKey(enemyLoc)) return scoreCache.get(enemyLoc);

		Unit[] nearby = enemyLoc.senseNearbyUnitsByFilter(1, unit -> unit.getLocation().isOnMap());
		int numEnemies = (int) Arrays.stream(nearby).filter(unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam()).count();
		int numFriendlies = nearby.length - numEnemies;

		int score = numEnemies - (3 * numFriendlies);
		scoreCache.put(enemyLoc, score);
		return score;
	}

	public static Unit[] getEnemyChunks() {
		if (GameController.INSTANCE.getRoundNumber() != lastRound) {
			update();
		}
		return enemyUnits;
	}
}
