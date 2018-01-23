package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.main.RoundInfo;

public class RangerExecutor implements UnitExecutor {
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
	
	@Override
	public void execute(Unit unit) {
		if (unit.isAttackReady()) {
			int bestDistanceSquared = Integer.MAX_VALUE;
			Unit bestTarget = null;
			int priorityIndex = 0;
			for (Unit enemyUnit : unit.senseNearbyUnitsByTeam(50, GameController.INSTANCE.getEnemyTeam())) {
				int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition()
						.getDistanceSquared(unit.getLocation().getMapLocation().getPosition());
				int unitPriority = getPriorityIndex(enemyUnit);
				if (distanceSquared > unit.getRangerCannotAttackRange() && distanceSquared < unit.getAttackRange()) {
					if (distanceSquared < unit.getAttackRange()) {
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
			}
			if (bestTarget != null) {
				if (unit.canAttack(bestTarget)) {
					unit.attack(bestTarget);
				}
			}
		}
		if(unit.isAbilityUnlocked() && unit.isBeginSnipeReady() && unit.getAbilityHeat() < 10) {
			if(unit.isAttackReady() && unit.isMoveReady()) {
				int furthestDistance = Integer.MIN_VALUE;
				Unit targetEnemy = null;
				for(Unit enemyUnit : RoundInfo.getEnemiesOnMap()) {
					int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition()
							.getDistanceSquared(unit.getLocation().getMapLocation().getPosition());
					if(distanceSquared > furthestDistance) {
						targetEnemy = enemyUnit;
					}
				}
				if(targetEnemy != null) {
					MapLocation targetLocation = targetEnemy.getLocation().getMapLocation();
					if(unit.canBeginSnipe(targetLocation)) {
						unit.beginSnipe(targetLocation);
					}
				}
			}
		}
	}
}
