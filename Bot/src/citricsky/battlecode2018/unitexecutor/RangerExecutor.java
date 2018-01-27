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
		if (unit.isRangerSniping()) {
			return;
		}
		if (unit.isAttackReady()) {
			double lowestHealthPercentage = 1.01;
			Unit bestTarget = null;
			int priorityIndex = 0;
			for (Unit enemyUnit : unit.senseNearbyUnitsByTeam(50, GameController.INSTANCE.getEnemyTeam())) {
				float healthPercentage = enemyUnit.getHealth()/enemyUnit.getMaxHealth();
				int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition()
						.getDistanceSquared(unit.getLocation().getMapLocation().getPosition());
				int unitPriority = getPriorityIndex(enemyUnit);
				if (distanceSquared > unit.getRangerCannotAttackRange() && distanceSquared < unit.getAttackRange()) {
					if (unitPriority > priorityIndex) {
						lowestHealthPercentage = healthPercentage;
						bestTarget = enemyUnit;
						priorityIndex = unitPriority;
					} else if (unitPriority == priorityIndex) {
						if (healthPercentage < lowestHealthPercentage) {
							lowestHealthPercentage = healthPercentage;
							bestTarget = enemyUnit;
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
		if(unit.isAbilityUnlocked() && unit.isBeginSnipeReady()) {
			if(unit.isAttackReady() && unit.isMoveReady()) {
				int furthestDistance = Integer.MIN_VALUE;
				Unit targetEnemy = null;
				for(Unit enemyUnit : RoundInfo.getEnemiesOnMap()) {
					int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition()
							.getDistanceSquared(unit.getLocation().getMapLocation().getPosition());
					if(distanceSquared > furthestDistance) {
						targetEnemy = enemyUnit;
						furthestDistance = distanceSquared;
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
