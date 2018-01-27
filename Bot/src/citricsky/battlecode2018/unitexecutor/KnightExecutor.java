package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;

public class KnightExecutor implements UnitExecutor {

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
			Unit targetEnemy = null;
			int priorityIndex = Integer.MIN_VALUE;
			double lowestHealthPercentage = 1.01;
			for(Unit enemy : unit.senseNearbyUnitsByTeam(2, GameController.INSTANCE.getEnemyTeam())){
				double healthPercentage = enemy.getHealth()/enemy.getMaxHealth();
				if(getPriorityIndex(enemy) > priorityIndex) {
					lowestHealthPercentage = healthPercentage;
					targetEnemy = enemy;
					priorityIndex = getPriorityIndex(enemy);
				}
				else if(getPriorityIndex(enemy) == priorityIndex) {
					if(healthPercentage < lowestHealthPercentage) {
						targetEnemy = enemy;
						lowestHealthPercentage = healthPercentage;
					}
				}
			}
			if (targetEnemy != null) {
				if (unit.canAttack(targetEnemy)) {
					unit.attack(targetEnemy);
				}
			}
		}
		if(unit.isAbilityUnlocked() && unit.isJavelinReady()){
			Unit targetEnemy = null;
			int priorityIndex = Integer.MIN_VALUE;
			double lowestHealthPercentage = 1.01;
			for (Unit enemyUnit : unit.senseNearbyUnitsByTeam(10, GameController.INSTANCE.getEnemyTeam())) {
				float healthPercentage = enemyUnit.getHealth()/enemyUnit.getMaxHealth();
				int unitPriority = getPriorityIndex(enemyUnit);
				if (unitPriority > priorityIndex) {
					lowestHealthPercentage = healthPercentage;
					targetEnemy = enemyUnit;
					priorityIndex = unitPriority;
				} else if (unitPriority == priorityIndex) {
					if (healthPercentage < lowestHealthPercentage) {
						lowestHealthPercentage = healthPercentage;
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
