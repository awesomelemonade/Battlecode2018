package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.main.RoundInfo;

public class RangerExecutor implements UnitExecutor {
	private static int getPriorityIndex(Unit unit) {
		UnitType unitType = unit.getType();
		if (unitType.equals(UnitType.WORKER)) {
			return 0;
		}
		if (unitType.equals(UnitType.FACTORY)) {
			return 1;
		}
		if (unitType.equals(UnitType.ROCKET)) {
			return 2;
		} else {
			return 1;
		}
	}
	private static int getAbilityPriorityIndex(Unit unit) {
		UnitType unitType = unit.getType();
		if (unitType.equals(UnitType.FACTORY)) {
			return 1;
		}
		if (unitType.equals(UnitType.ROCKET)) {
			return 2;
		} else {
			return 0;
		}
	}
	
	@Override
	public void postExecute(Unit unit) {
		if(unit.isAbilityUnlocked() && unit.isBeginSnipeReady()) {
			if(unit.isAttackReady() && unit.isMoveReady()) {
				int furthestDistance = Integer.MIN_VALUE;
				int priorityIndex = Integer.MIN_VALUE;
				Unit targetEnemy = null;
				for(Unit enemyUnit : RoundInfo.getEnemiesOnMap()) {
					int unitPriority = getAbilityPriorityIndex(unit);
					int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition()
							.getDistanceSquared(unit.getLocation().getMapLocation().getPosition());
					if(unitPriority > priorityIndex) {
						targetEnemy = enemyUnit;
						furthestDistance = distanceSquared;
						priorityIndex = unitPriority;
					}
					if(unitPriority == priorityIndex) {
						if(distanceSquared > furthestDistance) {
							targetEnemy = enemyUnit;
							furthestDistance = distanceSquared;
						}
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
	
	@Override
	public void execute(Unit unit) {
		if (unit.isRangerSniping()) {
			return;
		}
		if (unit.isAttackReady()) {
			Unit bestTarget = null;
			int priorityIndex = Integer.MIN_VALUE;
			int lowestHealth = Integer.MAX_VALUE;
			for (Unit enemyUnit : unit.senseNearbyUnitsByTeam(50, GameController.INSTANCE.getEnemyTeam())) {
				if (unit.canAttack(enemyUnit)) {
					int health = enemyUnit.getHealth();
					int unitPriority = getPriorityIndex(enemyUnit);
					if (unitPriority > priorityIndex) {
						bestTarget = enemyUnit;
						lowestHealth = health;
						priorityIndex = unitPriority;
					} else if (unitPriority == priorityIndex) {
						if (health < lowestHealth) {
							bestTarget = enemyUnit;
							lowestHealth = health;
						}
					}
				}
			}
			if (bestTarget != null) {
				unit.attack(bestTarget);
			}
		}
	}
}
