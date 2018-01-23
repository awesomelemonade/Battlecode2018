package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;

public class RangerExecutor implements UnitExecutor {
	@Override
	public void update() {
		
	}
	
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
			for (Unit enemyUnit : unit.senseNearbyUnitsByTeam(50, GameController.INSTANCE.getTeam())) {
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
	}
}
