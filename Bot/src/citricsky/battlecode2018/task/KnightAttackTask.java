package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.main.RoundInfo;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

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

	private Set<MapLocation> valid;

	public KnightAttackTask() {
		valid = new HashSet<MapLocation>();
	}

	private Predicate<MapLocation> stopCondition = location -> valid.contains(location);

	@Override
	public void update() {
		valid.clear();
		for (Unit unit : RoundInfo.getEnemiesOnMap()) {
			for (Direction direction : Direction.CARDINAL_DIRECTIONS) {
				MapLocation offset = unit.getLocation().getMapLocation().getOffsetLocation(direction);
				if (offset.isOnMap()) {
					valid.add(offset);
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
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}
