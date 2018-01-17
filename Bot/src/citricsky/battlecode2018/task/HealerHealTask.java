package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class HealerHealTask implements PathfinderTask {
	private Set<MapLocation> invalid;
	private Set<MapLocation> valid;
	private Unit[] friendlyUnits;
	private Predicate<MapLocation> stopCondition = location -> {
		if (invalid.contains(location)) {
			return false;
		}
		if (valid.contains(location)) {
			return true;
		}
		for (Unit friendlyUnit : friendlyUnits) {
			if (friendlyUnit.getHealth() == friendlyUnit.getMaxHealth()) continue;
			int distanceSquared = friendlyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			if (distanceSquared <= 30) {
				valid.add(location);
				return true;
			}
		}
		invalid.add(location);
		return false;
	};

	public HealerHealTask() {
		invalid = new HashSet<MapLocation>();
		valid = new HashSet<MapLocation>();
	}

	@Override
	public void update() {
		invalid.clear();
		valid.clear();
		friendlyUnits = GameController.INSTANCE.getMyUnitsByFilter(
				unit -> unit.getLocation().isOnMap() && !unit.isStructure());
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		Unit[] friendlyUnits = GameController.INSTANCE.getMyUnitsByFilter(
				friendly -> friendly.getLocation().isOnMap());
		int leastHealth = Integer.MIN_VALUE;
		Unit bestTarget = null;
		for (Unit friendlyUnit : friendlyUnits) {
			if (friendlyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition()) > 30) continue;
			int health = friendlyUnit.getHealth();
			if (health == friendlyUnit.getMaxHealth()) continue;
			if (health > leastHealth) {
				leastHealth = health;
				bestTarget = friendlyUnit;
			}
		}
		if (bestTarget != null) {
			if (unit.isHealReady() && unit.canHeal(bestTarget)) {
				unit.heal(bestTarget);
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}