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
