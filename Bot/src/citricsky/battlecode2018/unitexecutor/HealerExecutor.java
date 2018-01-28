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
			double leastAbilityPercentage = 1.1;
			Unit bestTarget = null;
			for (Unit friendlyUnit : RoundInfo.getMyUnits()) {
				if(friendlyUnit.getType().isStructure()) continue;
				if (unit.canOvercharge(friendlyUnit)) {
					double abilityPercentage = friendlyUnit.getAbilityHeat()/friendlyUnit.getAbilityCooldown();
					if(abilityPercentage < leastAbilityPercentage) {
						bestTarget = friendlyUnit;
						leastAbilityPercentage = abilityPercentage;
					}
				}
			}
			if(leastAbilityPercentage < 0.6) {
				unit.overcharge(bestTarget);
				moveManager.queueUnit(unit);
			}
		}
	}
}
