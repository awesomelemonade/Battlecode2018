package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class WorkerHarvestTask implements PathfinderTask {
	private Direction getHarvestDirection(MapLocation location) {
		for(Direction direction: Direction.values()) {
			MapLocation offset = location.getOffsetLocation(direction);
			if(!offset.isOnMap()) {
				continue;
			}
			if(GameController.INSTANCE.canSenseLocation(offset)) {
				if(offset.getKarboniteCount() > 0) {
					return direction;
				}
			}else {
				if(offset.getPlanet().getStartingMap().getInitialKarboniteAt(offset) > 0) {
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
				Direction direction = getHarvestDirection(location);
				if(unit.canHarvest(direction)) {
					unit.harvest(direction);
				}
			}
		}
	}
	
	@Override
	public boolean isStopCondition(MapLocation location) {
		return getHarvestDirection(location) != null;
	}
}
