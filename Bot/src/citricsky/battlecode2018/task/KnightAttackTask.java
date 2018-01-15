package citricsky.battlecode2018.task;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class KnightAttackTask implements PathfinderTask{

	private static final Predicate<MapLocation> STOP_CONDITION = location -> {
		return getEnemyDirection(location) != null;
	};
	private static Direction getEnemyDirection(MapLocation location) {
		Direction factoryDirection = null;
		for(Direction direction: Direction.CARDINAL_DIRECTIONS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if(GameController.INSTANCE.canSenseLocation(offset)) {
				if(offset.hasUnitAtLocation()) {
					Unit locationUnit = offset.getUnit();
					if(locationUnit.getTeam().equals(GameController.INSTANCE.getEnemyTeam())) {
						if(locationUnit.getType().equals(UnitType.FACTORY) && factoryDirection == null) {
							factoryDirection = direction;
						}
						else {
							return direction;
						}
					}
				}
			}
		}
		if(factoryDirection != null) {
			return factoryDirection;
		}
		return null;
	}
	@Override
	public void execute(Unit unit, MapLocation location) {
		if(unit.getLocation().getMapLocation().equals(location)) {
			Unit enemyUnit = location.getOffsetLocation(getEnemyDirection(location)).getUnit();
			if(unit.canAttack(enemyUnit)) {
				if(unit.isAttackReady()) {
					unit.attack(enemyUnit);
				}
			}
			if(unit.canJavelin(enemyUnit)) {
				if(unit.isJavelinReady()) {
					unit.javelin(enemyUnit);
				}
			}
		}
	}
	@Override
	public Predicate<MapLocation> getStopCondition() {
		return STOP_CONDITION;
	}
}
