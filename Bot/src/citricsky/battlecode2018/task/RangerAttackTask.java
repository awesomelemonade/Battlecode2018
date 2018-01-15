package citricsky.battlecode2018.task;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import bc.VecUnit;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class RangerAttackTask implements PathfinderTask {
	private Set<MapLocation> invalid;
	private Set<MapLocation> valid;
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
			if (distanceSquared < 40) {
				invalid.add(location);
				return false;
			}
		}
		for (Unit enemyUnit : enemyUnits) {
			int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			if (distanceSquared <= 50) {
				valid.add(location);
				return true;
			}
		}
		invalid.add(location);
		return false;
	};

	public RangerAttackTask() {
		invalid = new HashSet<MapLocation>();
		valid = new HashSet<MapLocation>();
	}

	@Override
	public void update() {
		invalid.clear();
		valid.clear();
		enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam() && unit.getLocation().isOnMap());
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		int bestDistanceSquared = Integer.MAX_VALUE;
		Unit bestTarget = null;

		VecUnit vecUnit = GameController.INSTANCE.getBcGameController().units();
		if (vecUnit != null) {
			for (int i = 0, len = (int) vecUnit.size(); i < len; ++i) {
				Unit enemyUnit = new Unit(vecUnit.get(i));
				if (enemyUnit.getTeam() == GameController.INSTANCE.getEnemyTeam() && enemyUnit.getLocation().isOnMap()) {
					int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
					if (distanceSquared > unit.getRangerCannotAttackRange()) {
						if (distanceSquared < bestDistanceSquared) {
							bestDistanceSquared = distanceSquared;
							bestTarget = enemyUnit;
						}
					}
				}
			}
		}

		if (bestTarget != null) {
			if (unit.isAttackReady() && unit.canAttack(bestTarget)) {
				unit.attack(bestTarget);
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}
