package citricsky.battlecode2018.unithandler;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;

public class WorkerHarvestTask implements PathfinderTask {
	private static final Predicate<MapLocation> STOP_CONDITION = location -> {
		if(GameController.INSTANCE.canSenseLocation(location)) {
			return location.getKarboniteCount()>0;
		}else {
			return location.getPlanet().getStartingMap().getInitialKarboniteAt(location)>0;
		}
	};
	@Override
	public void execute(Unit unit, MapLocation location, Direction direction) {
		if(unit.getLocation().getMapLocation().equals(location)) {
			if(!unit.hasWorkerActed() && unit.canHarvest(direction)) {
				unit.harvest(direction);
			}
		}
	}
	@Override
	public Predicate<MapLocation> getStopCondition() {
		return STOP_CONDITION;
	}
}
