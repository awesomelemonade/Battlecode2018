package citricsky.battlecode2018.task;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class WorkerRepairFactoryTask implements PathfinderTask {
	private static final Predicate<MapLocation> STOP_CONDITION = location -> WorkerRepairFactoryTask.getRepairTarget(location) != null;

	private static Unit getRepairTarget(MapLocation location) {
		for (Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					Unit unit = offset.getUnit();
					if ((unit.isStructure() && (!unit.isStructureBuilt()) &&
							unit.getTeam() == GameController.INSTANCE.getTeam())) {
						if(unit.getType() == UnitType.FACTORY) {
							if(unit.getHealth()<unit.getMaxHealth()) {
								return unit;
							}
						}
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
				Unit repairTarget = WorkerRepairFactoryTask.getRepairTarget(location);
				if (unit.canBuild(repairTarget)) {
					unit.build(repairTarget);
				}
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return STOP_CONDITION;
	}
}