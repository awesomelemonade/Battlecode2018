package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.main.RoundInfo;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.HashSet;
import java.util.Set;

public class KnightAttackTask implements PathfinderTask {
	private static Unit getEnemyUnit(MapLocation location) {
		Unit factoryUnit = null;
		for (Direction direction : Direction.CARDINAL_DIRECTIONS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					Unit locationUnit = offset.getUnit();
					if (locationUnit.getTeam().equals(GameController.INSTANCE.getEnemyTeam())) {
						if (locationUnit.getType().equals(UnitType.FACTORY) && factoryUnit == null) {
							factoryUnit = locationUnit;
						} else {
							return locationUnit;
						}
					}
				}
			}
		}
		return factoryUnit;
	}

	private Set<MapLocation> cache;

	public KnightAttackTask() {
		cache = new HashSet<MapLocation>();
	}

	@Override
	public void update() {
		cache.clear();
		for (Unit unit : RoundInfo.getEnemiesOnMap()) {
			for (Direction direction : Direction.CARDINAL_DIRECTIONS) {
				MapLocation offset = unit.getLocation().getMapLocation().getOffsetLocation(direction);
				if (offset.isOnMap()) {
					cache.add(offset);
				}
			}
		}
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (unit.getLocation().getMapLocation().equals(location)) {
			Unit enemyUnit = KnightAttackTask.getEnemyUnit(location);
			if(enemyUnit != null) {
				if (unit.isAttackReady() && unit.canAttack(enemyUnit)) {
					unit.attack(enemyUnit);
				}
			}
		}
		if(unit.isAbilityUnlocked() && unit.getAbilityHeat() < 10) {
			Unit bestTarget = null;
			boolean onlySeenFactory = true;
			int bestDistanceSquared = Integer.MAX_VALUE;
			for (Unit enemyUnit : RoundInfo.getEnemiesOnMap()) {
				int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
				if(distanceSquared < unit.getAttackRange()) {
					if(onlySeenFactory && enemyUnit.getType() != UnitType.FACTORY) {
						bestDistanceSquared = distanceSquared;
						bestTarget = enemyUnit;
						onlySeenFactory = false;
					}else {
						if(distanceSquared < bestDistanceSquared) {
							if(onlySeenFactory || (!onlySeenFactory && enemyUnit.getType() != UnitType.FACTORY)) {
								bestDistanceSquared = distanceSquared;
								bestTarget = enemyUnit;
							}
						}
					}
				}
			}
			if(bestTarget != null) {
				if(unit.canJavelin(bestTarget)) {
					unit.javelin(bestTarget);
				}
			}
		}
	}

	@Override
	public boolean isStopCondition(MapLocation location) {
		return cache.contains(location);
	}
}
