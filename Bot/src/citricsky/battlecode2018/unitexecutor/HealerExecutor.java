package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;

public class HealerExecutor implements UnitExecutor {
	@Override
	public void execute(Unit unit) {
		Unit[] friendlyUnits = unit.senseNearbyUnitsByTeam(30, GameController.INSTANCE.getTeam());
		if(unit.isHealReady()) {
			int leastHealth = Integer.MIN_VALUE;
			Unit bestTarget = null;
			for (Unit friendlyUnit : friendlyUnits) {
				int health = friendlyUnit.getHealth();
				if (health == friendlyUnit.getMaxHealth()) continue;
				if (health > leastHealth) {
					leastHealth = health;
					bestTarget = friendlyUnit;
				}
			}
			if (bestTarget != null) {
				if (unit.canHeal(bestTarget)) {
					unit.heal(bestTarget);
				}
			}
		}
		if (unit.isAbilityUnlocked() && unit.isOverchargeReady()) {
			for (Unit friendlyUnit : friendlyUnits) {
				if ((!friendlyUnit.isStructure()) && friendlyUnit.getAbilityHeat() > 60) {
					if (unit.canOvercharge(friendlyUnit)) {
						unit.overcharge(friendlyUnit);
						break;
					}
				}
			}
		}
	}
}
