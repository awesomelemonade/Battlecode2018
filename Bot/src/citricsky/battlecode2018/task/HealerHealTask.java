package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.HashSet;
import java.util.Set;

public class HealerHealTask implements PathfinderTask {
	private Set<MapLocation> invalid;
	private Set<MapLocation> valid;
	private Unit[] friendlyUnits;
	private Unit[] enemyUnits;

	public HealerHealTask() {
		invalid = new HashSet<MapLocation>();
		valid = new HashSet<MapLocation>();
	}

	@Override
	public void update() {
		invalid.clear();
		valid.clear();
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
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
		if(unit.isAbilityUnlocked() && unit.getAbilityHeat() < 10) {
			for(Unit friendlyUnit : friendlyUnits) {
				if(friendlyUnit.getAbilityHeat()>60) {
					unit.overcharge(friendlyUnit);
					break;
				}
			}
		}
	}

	@Override
	public boolean isStopCondition(MapLocation location) {
		if (invalid.contains(location)) {
			return false;
		}
		if (valid.contains(location)) {
			return true;
		}
		
		Unit unit = location.getUnit();
		enemyUnits = unit.senseNearbyUnitsByTeam(50, GameController.INSTANCE.getEnemyTeam());
		friendlyUnits = unit.senseNearbyUnitsByTeam(30, GameController.INSTANCE.getEnemyTeam());
		int validityPoints = 0;
		
		for (Unit friendlyUnit : friendlyUnits) {
			if (friendlyUnit.getHealth() == friendlyUnit.getMaxHealth()) continue;
			validityPoints+=2;
		}
		for (Unit enemyUnit : enemyUnits) {
			if(enemyUnit.getAttackRange()>=enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition())){
				validityPoints+=1;
			}
		}
		if(validityPoints>0) {
			valid.add(location);
			return true;
		}
		else {
			invalid.add(location);
			return false;
		}
	}
}