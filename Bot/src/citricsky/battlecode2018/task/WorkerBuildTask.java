package citricsky.battlecode2018.task;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class WorkerBuildTask implements PathfinderTask {
	public static WorkerBuildTask INSTANCE;
	static {
		INSTANCE = new WorkerBuildTask();
	}
	private static final Predicate<MapLocation> STOP_CONDITION = location -> WorkerBuildTask.getBuildDirection(location) != null;

	private static Direction getBuildDirection(MapLocation location) {
		for (Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					if ((offset.getUnit().isStructure() && (!offset.getUnit().isStructureBuilt()) &&
							offset.getUnit().getTeam() == GameController.INSTANCE.getTeam())) {
						return direction;
					}
				}
			}
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
