package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.main.EnemyMap;

public class MageExecutor implements UnitExecutor {
	private Unit[] enemyUnits;
	
	private Unit getAttackTarget(Unit unit) {
		enemyUnits = unit.senseNearbyUnitsByTeam(30, GameController.INSTANCE.getEnemyTeam());
		int bestScore = Integer.MIN_VALUE;
		Unit bestTarget = null;
 		for (Unit enemyUnit : enemyUnits) {
 			int thisScore = EnemyMap.getScore(enemyUnit);
			if (thisScore <= 0) continue;
			if (thisScore > bestScore) {
				bestScore = thisScore;
				bestTarget = enemyUnit;
			}
		}
		return bestTarget;
	}
	
	
	@Override
	public void execute(Unit unit) {
		if(unit.isAttackReady()) {
			Unit target = getAttackTarget(unit);
			if (target != null && unit.canAttack(target)) {
				unit.attack(target);
			}
		}
	}
}
