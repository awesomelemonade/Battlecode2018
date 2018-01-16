package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.main.EnemyMap;
import citricsky.battlecode2018.unithandler.PathfinderTask;

import java.util.function.Predicate;

public class RobotAdvanceTask implements PathfinderTask {
	private Unit[] enemyUnits;
	private Predicate<MapLocation> stopCondition = location -> getMoveDirection(location) != null;

	@Override
	public void update() {
		enemyUnits = EnemyMap.getEnemyChunks();
	}

	private Direction getMoveDirection(MapLocation location) {
		if (enemyUnits.length == 0) return null;
		if (EnemyMap.getScore(enemyUnits[0]) <= 0) return null;
		return location.getPosition().getDirectionTowards(enemyUnits[0].getLocation().getMapLocation().getPosition());
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (!unit.isMoveReady()) return;
		Direction direction = getMoveDirection(location);
		if (direction == null) return;

		if (unit.canMove(direction)) {
			unit.move(direction);
		} else if (unit.canMove(direction.rotateCounterClockwise())) {
			unit.move(direction.rotateCounterClockwise());
		} else if (unit.canMove(direction.rotateClockwise())) {
			unit.move(direction.rotateClockwise());
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}