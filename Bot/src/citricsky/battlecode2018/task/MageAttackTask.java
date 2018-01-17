package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.main.EnemyMap;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.*;

public class MageAttackTask implements PathfinderTask {
	private static final int MAGE_ATTACK_RANGE = 30;
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
		enemyUnits = EnemyMap.getEnemyChunks();
	}

	private Unit getAttackTarget(MapLocation location) {
 		for (Unit enemyUnit : enemyUnits) {
			if (EnemyMap.getScore(enemyUnit) <= 0) return null;
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
