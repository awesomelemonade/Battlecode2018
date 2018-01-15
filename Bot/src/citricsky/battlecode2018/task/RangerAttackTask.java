package citricsky.battlecode2018.task;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

import bc.VecUnit;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class RangerAttackTask implements PathfinderTask {
	private static final Predicate<MapLocation> STOP_CONDITION = location -> {
		Unit[] enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam() && unit.getLocation().isOnMap());
		Vector position = location.getPosition();

		Arrays.sort(enemyUnits, Comparator.comparingInt(unit -> unit.getLocation().getMapLocation().getPosition().getDistanceSquared(position)));
		int distanceSquared = enemyUnits[0].getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
		return distanceSquared <= 50 && !(distanceSquared < 40);
	};

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
		return STOP_CONDITION;
	}
}
