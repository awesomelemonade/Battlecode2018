package citricsky.battlecode2018.task;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class WorkerBuildTask implements PathfinderTask {
	private static final Predicate<MapLocation> STOP_CONDITION = location -> WorkerBuildTask.getBuildTarget(location) != null;

	private static Unit getBuildTarget(MapLocation location) {
		Unit factoryUnit = null;
		for (Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					Unit unit = offset.getUnit();
					if ((unit.isStructure() && (!unit.isStructureBuilt()) &&
							unit.getTeam() == GameController.INSTANCE.getTeam())) {
						if (unit.getType().equals(UnitType.FACTORY) && factoryUnit == null) {
							factoryUnit = unit;
						} else {
							return unit;
						}
					}
				}
			}
		}
		if (factoryUnit != null) {
			return factoryUnit;
		}
		return null;
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (unit.getLocation().getMapLocation().equals(location)) {
			if (!unit.hasWorkerActed()) {
				Unit buildTarget = WorkerBuildTask.getBuildTarget(location);
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
