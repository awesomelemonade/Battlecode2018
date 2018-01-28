package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.main.EnemyMap;
import citricsky.battlecode2018.main.RoundInfo;

public class MageExecutor implements UnitExecutor {
	private Unit getAttackTarget(Unit unit) {
		int bestScore = Integer.MIN_VALUE;
		Unit bestTarget = null;
 		for (Unit enemyUnit : RoundInfo.getEnemiesOnMap()) {
 			if (unit.canAttack(enemyUnit)) {
 				int score = EnemyMap.getScore(enemyUnit);
 				if (score <= 0) {
 					continue;
 				}
 				if (score > bestScore) {
 					bestScore = score;
 					bestTarget = enemyUnit;
 				}
 			}
		}
		return bestTarget;
	}
	
	
	@Override
	public void execute(Unit unit) {
		if(unit.isAttackReady()) {
			Unit target = getAttackTarget(unit);
			if (target != null) {
				unit.attack(target);
			}
		}
	}
}
