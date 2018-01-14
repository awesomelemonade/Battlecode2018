package citricsky.battlecode2018.unithandler;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;

public class WorkerBuildTask implements PathfinderTask {
	private static final Predicate<MapLocation> STOP_CONDITION = location -> {
		if(GameController.INSTANCE.canSenseLocation(location)) {
			if(location.hasUnitAtLocation()) {
				return (location.getUnit().isStructure() && 
						location.getUnit().getTeam() == GameController.INSTANCE.getTeam());
			}
		}
		return false;
	};
	@Override
	public void execute(Unit unit, MapLocation location, Direction direction) {
		if(unit.getLocation().getMapLocation().equals(location)) {
			if(!unit.hasWorkerActed()) {
				Unit buildTarget = location.getOffsetLocation(direction).getUnit();
				if(unit.canBuild(buildTarget)) {
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
