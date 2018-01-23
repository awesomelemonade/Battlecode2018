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
			int priorityIndex = 0;
			for(Unit enemy : unit.senseNearbyUnitsByTeam(2, GameController.INSTANCE.getEnemyTeam())){
				if(getPriorityIndex(enemy) > priorityIndex) {
					targetEnemy = enemy;
					priorityIndex = getPriorityIndex(enemy);
				}
			}if (targetEnemy != null) {
				if (unit.canAttack(targetEnemy)) {
					unit.attack(targetEnemy);
				}
			}
		}
		if(unit.isAbilityUnlocked() && unit.getAbilityHeat() < 10){
			Unit targetEnemy = null;
			int priorityIndex = 0;
			int bestDistanceSquared = Integer.MAX_VALUE;
			for (Unit enemyUnit : unit.senseNearbyUnitsByTeam(10, GameController.INSTANCE.getEnemyTeam())) {
				int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition()
						.getDistanceSquared(unit.getLocation().getMapLocation().getPosition());
				int unitPriority = getPriorityIndex(enemyUnit);
				if (distanceSquared <= unit.getAbilityRange()) {
					if (unitPriority > priorityIndex) {
						bestDistanceSquared = distanceSquared;
						targetEnemy = enemyUnit;
						priorityIndex = unitPriority;
					} else if (unitPriority == priorityIndex) {
						if (distanceSquared < bestDistanceSquared) {
							bestDistanceSquared = distanceSquared;
							targetEnemy = enemyUnit;
						}
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
