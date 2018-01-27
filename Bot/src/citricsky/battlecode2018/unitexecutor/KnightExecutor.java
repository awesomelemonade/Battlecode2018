package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;

public class KnightExecutor implements UnitExecutor {

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
	
	
	@Override
	public void execute(Unit unit) {
		if (unit.isAttackReady()) {
			Unit bestTarget = null;
			int priorityIndex = Integer.MIN_VALUE;
			int lowestHealth = Integer.MAX_VALUE;
			for (Unit enemyUnit : unit.senseNearbyUnitsByTeam(2, GameController.INSTANCE.getEnemyTeam())){
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
			for (Unit enemyUnit : unit.senseNearbyUnitsByTeam(10, GameController.INSTANCE.getEnemyTeam())) {
				int health = enemyUnit.getHealth();
				int unitPriority = getPriorityIndex(enemyUnit);
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
			if (targetEnemy != null) {
				if (unit.canJavelin(targetEnemy)) {
					unit.javelin(targetEnemy);
				}
			}
		}
	}
}
