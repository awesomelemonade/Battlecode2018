package citricsky.battlecode2018.task;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class RangerAttackTask implements PathfinderTask {
	private static final Predicate<MapLocation> STOP_CONDITION = location -> {
		Unit[] enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam());
		for(Unit enemyUnit: enemyUnits) {
			int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			if(distanceSquared>=40&&distanceSquared<=50) {
				return true;
			}
		}
		return false;
	};
	
	@Override
	public void execute(Unit unit, MapLocation location) {
		Unit[] enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				enemy -> enemy.getTeam() == GameController.INSTANCE.getEnemyTeam());
		int bestDistanceSquared = Integer.MAX_VALUE;
		Unit bestTarget = null;
		for(Unit enemyUnit: enemyUnits) {
			int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			if(distanceSquared>unit.getRangerCannotAttackRange()) {
				if(distanceSquared < bestDistanceSquared) {
					bestDistanceSquared = distanceSquared;
					bestTarget = enemyUnit;
				}
			}
		}
		if(bestTarget != null) {
			if(unit.isAttackReady() && unit.canAttack(bestTarget)) {
				unit.attack(bestTarget);
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return STOP_CONDITION;
	}
}
