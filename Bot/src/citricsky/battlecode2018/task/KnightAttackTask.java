package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.main.RoundInfo;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.HashSet;
import java.util.Set;


public class KnightAttackTask implements PathfinderTask {
	private static Unit getEnemyUnit(MapLocation location) {
		Unit factoryUnit = null;
		for (Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					Unit locationUnit = offset.getUnit();
					if (locationUnit.getTeam().equals(GameController.INSTANCE.getEnemyTeam())) {
						if (isLowerPriority(locationUnit) && factoryUnit == null) {
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

	private static boolean isLowerPriority(Unit unit) {
		if (unit.getType().equals(UnitType.FACTORY) || unit.getType().equals(UnitType.WORKER)) {
			return true;
		}
		return false;
	}

	private static int getPriorityIndex(Unit unit) {
		UnitType unitType = unit.getType();
		if (unitType.equals(UnitType.FACTORY) || unitType.equals(UnitType.WORKER)) {
			return 0;
		}
		if (unitType.equals(UnitType.ROCKET)) {
			return 2;
		} else {
			return 1;
		}
	}

	private Set<MapLocation> cache;

	public KnightAttackTask() {
		cache = new HashSet<MapLocation>();
	}

	@Override
	public void update() {
		cache.clear();
		for (Unit unit : RoundInfo.getEnemiesOnMap()) {
			for (Direction direction : Direction.COMPASS) {
				MapLocation offset = unit.getLocation().getMapLocation().getOffsetLocation(direction);
				if (offset.isOnMap()) {
					cache.add(offset);
				}
			}
		}
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (unit.isAttackReady()) {
			if (unit.getLocation().getMapLocation().equals(location)) {
				Unit enemyUnit = KnightAttackTask.getEnemyUnit(location);
				if (enemyUnit != null) {
					if (unit.canAttack(enemyUnit)) {
						unit.attack(enemyUnit);
					}
				}
			}
		}
		if (unit.isAbilityUnlocked() && unit.getAbilityHeat() < 10) {
			Unit bestTarget = null;
			int priorityIndex = 0;
			int bestDistanceSquared = Integer.MAX_VALUE;
			for (Unit enemyUnit : unit.senseNearbyUnitsByTeam(10, GameController.INSTANCE.getEnemyTeam())) {
				int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition()
						.getDistanceSquared(location.getPosition());
				int unitPriority = getPriorityIndex(enemyUnit);
				if (distanceSquared <= unit.getAbilityRange()) {
					if (unitPriority > priorityIndex) {
						bestDistanceSquared = distanceSquared;
						bestTarget = enemyUnit;
						priorityIndex = unitPriority;
					} else if (unitPriority == priorityIndex) {
						if (distanceSquared < bestDistanceSquared) {
							bestDistanceSquared = distanceSquared;
							bestTarget = enemyUnit;
						}
					}
				}
			}
			if (bestTarget != null) {
				if (unit.canJavelin(bestTarget)) {
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
