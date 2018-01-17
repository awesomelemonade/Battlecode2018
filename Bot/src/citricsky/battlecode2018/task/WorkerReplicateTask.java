package citricsky.battlecode2018.task;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.unithandler.PathfinderTask;
import citricsky.battlecode2018.util.Constants;
import citricsky.battlecode2018.util.Util;

public class WorkerReplicateTask implements PathfinderTask {
	private Direction getReplicateDirection(MapLocation location) {
		Direction bestDirection = null;
		int bestDistance = Integer.MAX_VALUE;
		for(Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if(!offset.isOnMap()) {
				continue;
			}
			if (offset.isPassableTerrain() && offset.isOccupiable()) {
				int distance = getNearestFriendlyStructureDistance(offset);
				if(distance<bestDistance) {
					bestDistance = distance;
					bestDirection = direction;
				}
			}
		}
		return bestDirection;
	}
	public int getNearestFriendlyStructureDistance(MapLocation location) {
		int bestDistance = Integer.MAX_VALUE;
		for(Unit structure: friendlyStructures) {
			int distance = Util.getMovementDistance(structure.getLocation().getMapLocation().getPosition(), location.getPosition());
			if(distance < bestDistance) {
				bestDistance = distance;
			}
		}
		return bestDistance;
	}
	private static final int MIN_WORKERS = 3;
	private Unit[] friendlyStructures;
	private int workerCount = 0;
	private int factoryCount = 0;
	private Predicate<MapLocation> stopCondition = location -> {
		if (GameController.INSTANCE.getCurrentKarbonite() < Constants.WORKER_REPLICATE_COST) {
			return false;
		}
		if (factoryCount > 0 && workerCount >= MIN_WORKERS && GameController.INSTANCE.getCurrentKarbonite() < 150) {
			return false;
		}
		return getReplicateDirection(location) != null;
	};
	@Override
	public void update() {
		friendlyStructures = GameController.INSTANCE.getMyUnitsByFilter(unit -> unit.isStructure());
		workerCount = GameController.INSTANCE.getMyUnitsByFilter(unit -> unit.getType() == UnitType.WORKER).length;
		factoryCount = GameController.INSTANCE.getMyUnitsByFilter(unit -> unit.getType() == UnitType.FACTORY).length;
	}
	@Override
	public void execute(Unit unit, MapLocation location) {
		if(unit.getLocation().getMapLocation().equals(location)) {
			if(!unit.hasWorkerActed()) {
				Direction direction = getReplicateDirection(location);
				if(unit.canReplicate(direction)) {
					workerCount++;
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
