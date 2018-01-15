package citricsky.battlecode2018.task;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class WorkerHarvestTask implements PathfinderTask {
	private static final Predicate<MapLocation> STOP_CONDITION = location -> {
		return WorkerHarvestTask.getHarvestDirection(location) != null;
	};
	private static Direction getHarvestDirection(MapLocation location) {
		for(Direction direction: Direction.values()) {
			MapLocation offset = location.getOffsetLocation(direction);
			if(GameController.INSTANCE.canSenseLocation(offset)) {
				if(location.getKarboniteCount() > 0) {
					return direction;
				}
			}else {
				if(location.getPlanet().getStartingMap().getInitialKarboniteAt(location) > 0) {
					return direction;
				}
			}
		}
		return null;
	}
	@Override
	public void execute(Unit unit, MapLocation location) {
		if(unit.getLocation().getMapLocation().equals(location)) {
			if(!unit.hasWorkerActed()) {
				Direction direction = WorkerHarvestTask.getHarvestDirection(location);
				if(unit.canHarvest(direction)) {
					unit.harvest(direction);
				}
			}
		}
	}
	@Override
	public Predicate<MapLocation> getStopCondition() {
		return STOP_CONDITION;
	}
}
