package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class MageAttackTask implements PathfinderTask {
	private static final Predicate<MapLocation> STOP_CONDITION = location -> {
		return getAttackLocation(location) != null;
	};

	private static MapLocation getAttackLocation(MapLocation location) {
		int[][] enemyChunks = new int[location.getPlanet().getWidth()][location.getPlanet().getHeight()]; // 9x9 chunks: center x, center y, score
		Unit[] enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam() && unit.getLocation().isOnMap());

		Set<MapLocation> seen = new HashSet<>();

		MapLocation bestLoc = null;
		int bestScore = -1;
		for (Unit enemyUnit : enemyUnits) {
			if (enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition()) > 30)
				continue;
			MapLocation enemyLoc = enemyUnit.getLocation().getMapLocation();

			for (Direction dir : Direction.DIAGONAL_CENTER) {
				MapLocation loc = enemyLoc.getOffsetLocation(dir);
				if (seen.contains(loc)) continue;
				seen.add(loc);

				Unit[] nearby = loc.senseNearbyUnitsByFilter(1, unit -> unit.getLocation().isOnMap());
				int numEnemies = (int) Arrays.stream(nearby).filter(unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam()).count();
				int numFriendlies = nearby.length - numEnemies;

				int score = numEnemies - (3 * numFriendlies);
				if (score > bestScore) {
					bestScore = score;
					bestLoc = loc;
				}
			}

		}

		return bestLoc;
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		Unit[] enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				enemy -> enemy.getTeam() == GameController.INSTANCE.getEnemyTeam() && enemy.getLocation().isOnMap());
		int bestDistanceSquared = Integer.MAX_VALUE;
		Unit bestTarget = null;
		for (Unit enemyUnit : enemyUnits) {
			int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			if (distanceSquared > unit.getRangerCannotAttackRange()) {
				if (distanceSquared < bestDistanceSquared) {
					bestDistanceSquared = distanceSquared;
					bestTarget = enemyUnit;
				}
			}
		}
		if (bestTarget != null) {
			if (unit.isAttackReady() && unit.canAttack(bestTarget)) {
				unit.attack(bestTarget);
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return STOP_CONDITION;
	}
}
