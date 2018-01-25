package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;

public class HealerExecutor implements UnitExecutor {
	@Override
	public void execute(Unit unit) {
		Unit[] friendlyUnits = unit.senseNearbyUnitsByTeam(30, GameController.INSTANCE.getTeam());
		if(unit.isHealReady()) {
			double leastHealthPercentage = 1.1;
			Unit bestTarget = null;
			for (Unit friendlyUnit : friendlyUnits) {
				double healthPercentage = friendlyUnit.getHealth() / friendlyUnit.getMaxHealth();
				if (healthPercentage == 1) continue;
				if (healthPercentage < leastHealthPercentage) {
					bestTarget = friendlyUnit;
					leastHealthPercentage = healthPercentage;
				}
			}
			if (bestTarget != null) {
				if (unit.canHeal(bestTarget)) {
					unit.heal(bestTarget);
				}
			}
		}
		if (unit.isAbilityUnlocked() && unit.isOverchargeReady()) {
			double leastAbilityPercentage = 1.1;
			Unit bestTarget = null;
			for (Unit friendlyUnit : friendlyUnits) {
				if(friendlyUnit.getType().isStructure()) continue;
				double abilityPercentage = friendlyUnit.getAbilityHeat()/friendlyUnit.getAbilityCooldown();
				if(abilityPercentage < leastAbilityPercentage) {
					bestTarget = friendlyUnit;
					leastAbilityPercentage = abilityPercentage;
				}
			}
			if(leastAbilityPercentage < 0.6) {
				if(!bestTarget.getType().isStructure() && unit.canOvercharge(bestTarget)) {
					unit.overcharge(bestTarget);
				}
			}
		}
	}
}
