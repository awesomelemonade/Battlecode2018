package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.main.EnemyMap;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.*;

public class MageAttackTask implements PathfinderTask {
	private Set<MapLocation> valid;
	private Set<MapLocation> invalid;
	private Unit[] enemyUnits;

	public MageAttackTask() {
		valid = new HashSet<>();
		invalid = new HashSet<>();
	}

	@Override
	public void update() {
		valid.clear();
		invalid.clear();
	}

	private Unit getAttackTarget(MapLocation location) {
		enemyUnits = location.getUnit().senseNearbyUnitsByTeam(30, GameController.INSTANCE.getTeam());
		int bestScore = Integer.MIN_VALUE;
		Unit bestTarget = null;
 		for (Unit enemyUnit : enemyUnits) {
 			int thisScore = EnemyMap.getScore(enemyUnit);
			if (thisScore <= 0) return null;
			if (thisScore > bestScore) {
				bestScore = thisScore;
				bestTarget = enemyUnit;
			}
		}
		return bestTarget;
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		if(unit.isAttackReady()) {
			if (unit.getLocation().getMapLocation().equals(location)) {
				Unit target = getAttackTarget(location);
				if (unit.canAttack(target)) {
					unit.attack(target);
				}
			}
		}
	}

	@Override
	public boolean isStopCondition(MapLocation location) {
		if (valid.contains(location)) return true;
		if (invalid.contains(location)) return false;

		if (getAttackTarget(location) != null) {
			valid.add(location);
			return true;
		} else {
			invalid.add(location);
			return false;
		}
	}
}
