package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.main.RoundInfo;

public class KnightExecutor implements UnitExecutor {

	private static int getPriorityIndex(Unit unit) {
		UnitType unitType = unit.getType();
		if (unitType.equals(UnitType.WORKER)) {
			return 0;
		}
		if (unitType.equals(UnitType.FACTORY)) {
			return 1;
		}
		if(unitType.equals(UnitType.KNIGHT)) {
			return 2;
		}
		if(unitType.equals(UnitType.RANGER)){
			return 3;
		}
		if(unitType.equals(UnitType.MAGE)) {
			return 4;
		}
		return 5; //if rocket
	}
	private static int getAbilityPriorityIndex(Unit unit) {
		UnitType unitType = unit.getType();
		if (unitType.equals(UnitType.WORKER)) {
			return 0;
		}
		if (unitType.equals(UnitType.FACTORY)) {
			return 1;
		}
		if(unitType.equals(UnitType.MAGE)) {
			return 3;
		}
		if (unitType.equals(UnitType.ROCKET)) {
			return 4;
		} else {
			return 2;
		}
	}
	
	
	@Override
	public void execute(Unit unit) {
		if (unit.isAttackReady()) {
			Unit bestTarget = null;
			int priorityIndex = Integer.MIN_VALUE;
			int lowestHealth = Integer.MAX_VALUE;
			for (Direction direction: Direction.COMPASS) {
				MapLocation offset = unit.getLocation().getMapLocation().getOffsetLocation(direction);
				if (!offset.hasUnitAtLocation()) {
					continue;
				}
				Unit enemyUnit = offset.getUnit();
				if (enemyUnit.getTeam() == GameController.INSTANCE.getTeam()) {
					continue;
				}
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
		if(unit.isAbilityUnlocked() && unit.isJavelinReady()){
			Unit targetEnemy = null;
			int priorityIndex = Integer.MIN_VALUE;
			int lowestHealth = Integer.MAX_VALUE;
			for (Unit enemyUnit : RoundInfo.getEnemiesOnMap()) {
				if (unit.canJavelin(enemyUnit)) {
					int health = enemyUnit.getHealth();
					int unitPriority = getAbilityPriorityIndex(enemyUnit);
					if (unitPriority > priorityIndex) {
						lowestHealth = health;
						targetEnemy = enemyUnit;
						priorityIndex = unitPriority;
					} else if (unitPriority == priorityIndex) {
						if (health < lowestHealth) {
							lowestHealth = health;
							targetEnemy = enemyUnit;
						}
					}
				}
			}
			if (targetEnemy != null) {
				unit.javelin(targetEnemy);
			}
		}
	}
}
