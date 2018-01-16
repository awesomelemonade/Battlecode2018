package citricsky.battlecode2018.task;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class WorkerReplicateTask implements PathfinderTask {
	private static final int MAX_WORKERS = 6;
	private Predicate<MapLocation> stopCondition = location -> {
		if (location.senseNearbyUnitsByFilter(2, unit -> unit.getTeam() == GameController.INSTANCE.getTeam() &&
				unit.getType() == UnitType.WORKER).length > MAX_WORKERS) return false;
		return WorkerReplicateTask.getReplicateDirection(location) != null;
	};
	private static Direction getReplicateDirection(MapLocation location) {
		for(Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if(!offset.isOnMap()) {
				continue;
			}
			if (offset.isPassableTerrain() && offset.isOccupiable()) {
				return direction;
			}
		}
		return null;
	}
	@Override
	public void execute(Unit unit, MapLocation location) {
		if(unit.getLocation().getMapLocation().equals(location)) {
			if(!unit.hasWorkerActed()) {
				Direction direction = WorkerReplicateTask.getReplicateDirection(location);
				if(unit.canReplicate(direction)) {
					unit.replicate(direction);
				}
			}
		}
	}
	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}
