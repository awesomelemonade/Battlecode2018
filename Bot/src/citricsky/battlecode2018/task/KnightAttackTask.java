package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class KnightAttackTask implements PathfinderTask {
	private static Unit getEnemyUnit(MapLocation location) {
		Unit factoryUnit = null;
		for (Direction direction : Direction.CARDINAL_DIRECTIONS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					Unit locationUnit = offset.getUnit();
					if (locationUnit.getTeam().equals(GameController.INSTANCE.getEnemyTeam())) {
						if (locationUnit.getType().equals(UnitType.FACTORY) && factoryUnit == null) {
							factoryUnit = locationUnit;
						} else {
							return locationUnit;
						}
					}
				}
			}
		}
		return factoryUnit;
	}

	private Set<MapLocation> valid;
	private Unit[] enemyUnits;

	public KnightAttackTask() {
		valid = new HashSet<MapLocation>();
	}

	private Predicate<MapLocation> stopCondition = location -> valid.contains(location);

	@Override
	public void update() {
		valid.clear();
		enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
				enemy -> enemy.getTeam() == GameController.INSTANCE.getEnemyTeam() && enemy.getLocation().isOnMap());
		for (Unit unit : enemyUnits) {
			for (Direction direction : Direction.CARDINAL_DIRECTIONS) {
				MapLocation offset = unit.getLocation().getMapLocation().getOffsetLocation(direction);
				if (offset.isOnMap()) {
					valid.add(offset);
				}
			}
		}
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (unit.getLocation().getMapLocation().equals(location)) {
			Unit enemyUnit = KnightAttackTask.getEnemyUnit(location);
			if(enemyUnit != null) {
				if (unit.isAttackReady() && unit.canAttack(enemyUnit)) {
					unit.attack(enemyUnit);
				}
			}
		}
		if(unit.isAbilityUnlocked() && unit.getAbilityHeat()<10) {
			Unit bestUnit = null;
			boolean onlySeenFactory = true;
			int bestDistanceSquared = Integer.MAX_VALUE;
			for(Unit enemyUnit: enemyUnits) {
				int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition()
						.getDistanceSquared(unit.getLocation().getMapLocation().getPosition());
				if(onlySeenFactory && enemyUnit.getType()!=UnitType.FACTORY) {
					bestDistanceSquared = distanceSquared;
					bestUnit = enemyUnit;
					onlySeenFactory = false;
				}
				else if(distanceSquared < bestDistanceSquared) {
					if((onlySeenFactory && enemyUnit.getType().equals(UnitType.FACTORY)) || 
							(!onlySeenFactory && enemyUnit.getType()!=UnitType.FACTORY)) {
						bestDistanceSquared = distanceSquared;
						bestUnit = enemyUnit;
					}
				}
			}
			if(bestUnit != null) {
				if(unit.canJavelin(bestUnit)) {
					unit.javelin(bestUnit);
				}
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}
