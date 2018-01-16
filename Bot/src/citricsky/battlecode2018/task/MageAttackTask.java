package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.*;
import java.util.function.Predicate;

public class MageAttackTask implements PathfinderTask {
	private static final int MAGE_ATTACK_RANGE = 30;
	private static Map<MapLocation, Integer> scoreCache;
	private Set<MapLocation> valid;
	private Set<MapLocation> invalid;
	private Unit[] enemyUnits;

	private Predicate<MapLocation> stopCondition = location -> {
		if (valid.contains(location)) return true;
		if (invalid.contains(location)) return false;

		if (getAttackTarget(location) != null) {
			valid.add(location);
			return true;
		} else {
			invalid.add(location);
			return false;
		}
	};

	public MageAttackTask() {
		scoreCache = new HashMap<>();
		valid = new HashSet<>();
		invalid = new HashSet<>();
		enemyUnits = updateEnemies();
	}

	@Override
	public void update() {
		scoreCache.clear();
		valid.clear();
		invalid.clear();
		enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam() && unit.getLocation().isOnMap());
	}

	private Unit[] updateEnemies() {
		Unit[] enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam() && unit.getLocation().isOnMap());

		Arrays.sort(enemyUnits, (unit1, unit2) -> Integer.compare(getScore(unit2), getScore(unit1))); // Purposefully flipped, to sort largest -> smallest
		return enemyUnits;
	}

	private static int getScore(Unit enemyUnit) {
		MapLocation enemyLoc = enemyUnit.getLocation().getMapLocation();
		if (scoreCache.containsKey(enemyLoc)) return scoreCache.get(enemyLoc);

		Unit[] nearby = enemyLoc.senseNearbyUnitsByFilter(1, unit -> unit.getLocation().isOnMap());
		int numEnemies = (int) Arrays.stream(nearby).filter(unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam()).count();
		int numFriendlies = nearby.length - numEnemies;

		int score = numEnemies - (3 * numFriendlies);
		scoreCache.put(enemyLoc, score);
		return score;
	}

	private Unit getAttackTarget(MapLocation location) {
 		for (Unit enemyUnit : enemyUnits) {
			if (getScore(enemyUnit) <= 0) return null;
			if (enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition()) < MAGE_ATTACK_RANGE)
				return enemyUnit;
		}
		return null;
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (unit.getLocation().getMapLocation().equals(location)) {
			Unit target = getAttackTarget(location);
			if (unit.isAttackReady() && unit.canAttack(target)) {
				unit.attack(target);
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}
