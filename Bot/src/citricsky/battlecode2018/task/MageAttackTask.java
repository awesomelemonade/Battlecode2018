package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class MageAttackTask implements PathfinderTask {
	private static final Predicate<MapLocation> STOP_CONDITION = location -> getAttackTarget(location) != null;

	private static Unit getAttackTarget(MapLocation location) {
		Unit[] enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam() && unit.getLocation().isOnMap());

		Set<MapLocation> seen = new HashSet<>();

		Unit bestEnemy = null;
		int bestScore = -1;
		for (Unit enemyUnit : enemyUnits) {
			if (enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition()) > 30)
				continue;
			MapLocation enemyLoc = enemyUnit.getLocation().getMapLocation();
			if (seen.contains(enemyLoc)) continue;
			seen.add(enemyLoc);

			Unit[] nearby = enemyLoc.senseNearbyUnitsByFilter(1, unit -> unit.getLocation().isOnMap());
			int numEnemies = (int) Arrays.stream(nearby).filter(unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam()).count();
			int numFriendlies = nearby.length - numEnemies;

			int score = numEnemies - (3 * numFriendlies);
			if (score > bestScore) {
				bestScore = score;
				bestEnemy = enemyUnit;
			}

		}

		return bestEnemy;
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (unit.getLocation().getMapLocation().equals(location)) {
			Unit target = getAttackTarget(location);
			if (!unit.isAttackReady() && unit.canAttack(target)) {
				unit.attack(target);
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return STOP_CONDITION;
	}
}
