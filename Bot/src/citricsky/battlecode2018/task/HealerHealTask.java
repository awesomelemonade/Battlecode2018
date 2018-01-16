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
	private Unit[] enemyUnits;
	private Predicate<MapLocation> stopCondition = location -> {
		if (invalid.contains(location)) {
			return false;
		}
		if (valid.contains(location)) {
			return true;
		}
		for (Unit enemyUnit : enemyUnits) {
			int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			if (distanceSquared < 10) {
				invalid.add(location);
				return false;
			}
		}
		for (Unit friendlyUnit : friendlyUnits) {
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
				unit -> unit.getLocation().isOnMap());
		enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam() && unit.getLocation().isOnMap());
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		Unit[] friendlyUnits = GameController.INSTANCE.getMyUnitsByFilter(
				friendly -> friendly.getLocation().isOnMap());
		int bestDistanceSquared = Integer.MAX_VALUE;
		Unit bestTarget = null;
		for (Unit friendlyUnit : friendlyUnits) {
			int distanceSquared = friendlyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			if (distanceSquared < bestDistanceSquared) {
				bestDistanceSquared = distanceSquared;
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