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

public class WorkerBlueprintFactoryTask implements PathfinderTask {
	private static final Predicate<MapLocation> PASSABLE_PREDICATE = location -> {
		if(location.isPassableTerrain()) {
			return true;
		}
		if(location.hasUnitAtLocation()) {
			if(location.getUnit().getTeam() == GameController.INSTANCE.getTeam()) {
				if(location.getUnit().isStructure()) {
					return false;
				}
			}
		}
		return true;
	};
	private static final Predicate<MapLocation> STOP_CONDITION = location -> {
		if(GameController.INSTANCE.getCurrentKarbonite() < Constants.FACTORY_COST) {
			return false;
		}
		return Util.canBuild(Util.getNeighbors(location, PASSABLE_PREDICATE));
	};
	@Override
	public void execute(Unit unit, MapLocation location, Direction direction) {
		if(unit.getLocation().getMapLocation().equals(location)) {
			if(!unit.hasWorkerActed() && unit.canBlueprint(UnitType.FACTORY, direction)) {
				unit.blueprint(UnitType.FACTORY, direction);
			}
		}
	}
	@Override
	public Predicate<MapLocation> getStopCondition() {
		return STOP_CONDITION;
	}
}
