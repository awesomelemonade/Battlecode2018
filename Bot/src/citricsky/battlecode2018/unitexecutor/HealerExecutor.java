package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.main.MoveManager;
import citricsky.battlecode2018.main.RoundInfo;

public class HealerExecutor implements UnitExecutor {
	private MoveManager moveManager;
	
	public HealerExecutor(MoveManager moveManager) {
		this.moveManager = moveManager;
	}
	
	private static int getPriorityIndex(Unit unit) {
		UnitType unitType = unit.getType();
		if (unitType.equals(UnitType.WORKER)) {
			return 0;
		}
		return 1;
	}
	
	@Override
	public void execute(Unit unit) {
		if(unit.isHealReady()) {
			double leastHealthPercentage = 1.0;
			Unit bestTarget = null;
			int priorityIndex = Integer.MIN_VALUE;
			for (Unit friendlyUnit : RoundInfo.getMyUnits()) {
				if (unit.canHeal(friendlyUnit)) {
					int unitPriority = getPriorityIndex(friendlyUnit);
					double healthPercentage = ((double)friendlyUnit.getHealth()) / ((double)friendlyUnit.getMaxHealth());
					if(priorityIndex < unitPriority && healthPercentage < 0.95) {
						leastHealthPercentage = healthPercentage;
						bestTarget = friendlyUnit;
						priorityIndex = unitPriority;
					}
					if(priorityIndex == unitPriority) {
						if (healthPercentage < leastHealthPercentage) {
							bestTarget = friendlyUnit;
							leastHealthPercentage = healthPercentage;
						}
					}
				}
			}
			if (bestTarget != null) {
				unit.heal(bestTarget);
			}
		}
		if (unit.isAbilityUnlocked() && unit.isOverchargeReady()) {
			int bestAbilityHeat = 0;
			int bestAttackHeat = 0;
			Unit bestTarget = null;
			int bestStepsToEnemy = Integer.MAX_VALUE;
			for (Unit friendlyUnit : RoundInfo.getMyUnits()) {
				if (!friendlyUnit.getLocation().isOnMap()) continue;
				if (friendlyUnit.getType().isStructure()) continue;
				if (unit.canOvercharge(friendlyUnit)) {
					int stepsToEnemy = moveManager.getBFSStep(MoveManager.BFS_FIND_COMBAT_ENEMY, friendlyUnit.getLocation().getMapLocation().getPosition());
					int abilityHeat = friendlyUnit.getAbilityHeat();
					int attackHeat = friendlyUnit.getAttackHeat();
					if (abilityHeat > bestAbilityHeat) {
						bestTarget = friendlyUnit;
						bestAbilityHeat = abilityHeat;
						bestStepsToEnemy = stepsToEnemy;
						bestAttackHeat = attackHeat;
					} else if (abilityHeat == bestAbilityHeat && attackHeat > bestAttackHeat) {
						bestTarget = friendlyUnit;
						bestAbilityHeat = abilityHeat;
						bestStepsToEnemy = stepsToEnemy;
						bestAttackHeat = attackHeat;
					}else if (abilityHeat == bestAbilityHeat && attackHeat == bestAttackHeat && stepsToEnemy < bestStepsToEnemy) {
						bestTarget = friendlyUnit;
						bestAbilityHeat = abilityHeat;
						bestStepsToEnemy = stepsToEnemy;
						bestAttackHeat = attackHeat;
					}
				}
			}
			if(bestTarget != null) {
				unit.overcharge(bestTarget);
				moveManager.queueUnit(unit);
			}
		}
	}
}
