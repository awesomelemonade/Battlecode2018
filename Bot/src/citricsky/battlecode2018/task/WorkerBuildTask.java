package citricsky.battlecode2018.task;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class WorkerBuildTask implements PathfinderTask {
	private static final Predicate<MapLocation> STOP_CONDITION = location -> WorkerBuildTask.getBuildDirection(location) != null;

	private static Direction getBuildDirection(MapLocation location) {
		Direction factoryDirection = null;
		for (Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					if ((offset.getUnit().isStructure() && (!offset.getUnit().isStructureBuilt()) &&
							offset.getUnit().getTeam() == GameController.INSTANCE.getTeam())) {
						if (offset.getUnit().getType().equals(UnitType.FACTORY) && factoryDirection == null) {
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

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (unit.getLocation().getMapLocation().equals(location)) {
			if (!unit.hasWorkerActed()) {
				Direction direction = WorkerBuildTask.getBuildDirection(location);
				Unit buildTarget = location.getOffsetLocation(direction).getUnit();
				if (unit.canBuild(buildTarget)) {
					unit.build(buildTarget);
				}
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return STOP_CONDITION;
	}
}
