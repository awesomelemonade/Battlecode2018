package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class KnightAttackTask implements PathfinderTask {
	private static Direction getEnemyDirection(MapLocation location) {
		Direction factoryDirection = null;
		for (Direction direction : Direction.CARDINAL_DIRECTIONS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					Unit locationUnit = offset.getUnit();
					if (locationUnit.getTeam().equals(GameController.INSTANCE.getEnemyTeam())) {
						if (locationUnit.getType().equals(UnitType.FACTORY) && factoryDirection == null) {
							factoryDirection = direction;
						} else {
							return direction;
						}
					}
				}
			}
		}
		if (factoryDirection != null) {
			return factoryDirection;
		}
		return null;
	}

	private Set<MapLocation> valid;

	public KnightAttackTask() {
		valid = new HashSet<MapLocation>();
	}

	private Predicate<MapLocation> stopCondition = location -> valid.contains(location);

	@Override
	public void update() {
		valid.clear();
		Unit[] enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(
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
			Unit enemyUnit = location.getOffsetLocation(getEnemyDirection(location)).getUnit();
			if (unit.canAttack(enemyUnit)) {
				if (unit.isAttackReady()) {
					unit.attack(enemyUnit);
				}
			}
			if (unit.canJavelin(enemyUnit)) {
				if (unit.isJavelinReady()) {
					unit.javelin(enemyUnit);
				}
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}
