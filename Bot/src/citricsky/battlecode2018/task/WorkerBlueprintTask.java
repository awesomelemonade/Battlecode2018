package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.unithandler.PathfinderTask;
import citricsky.battlecode2018.util.Constants;
import citricsky.battlecode2018.util.Util;

public class WorkerBlueprintTask implements PathfinderTask {
	private int numWorkers;
	private int numFactories;
	private int numBuiltFactories;
	private int numRockets;
	private int numBuiltRockets;

	private Direction getBlueprintDirection(MapLocation location) {
		int bestCounter = Integer.MIN_VALUE;
		Direction bestDirection = null;
		for (Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (!Util.PASSABLE_PREDICATE.test(offset)) {
				continue;
			}
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					continue;
				}
			}
			int counter = Util.getBuildArray(Util.getNeighbors(offset, Util.PASSABLE_PREDICATE));
			if(counter > bestCounter) {
				bestCounter = counter;
				bestDirection = direction;
			}
		}
		if(bestCounter>0) {
			return bestDirection;
		}else {
			return null;
		}
	}

	private UnitType getBlueprintType() {
		if (numFactories < 2 || numBuiltFactories < 1) return UnitType.FACTORY;
		if (numWorkers < 4) return UnitType.FACTORY;
		if (numBuiltRockets > numFactories) return UnitType.FACTORY;
		if (GameController.INSTANCE.getRoundNumber() < 600) return UnitType.FACTORY;
		return UnitType.ROCKET;
	}

	@Override
	public void update() {
		Unit[] structures = GameController.INSTANCE.getMyUnitsByFilter(Unit::isStructure);
		numWorkers = GameController.INSTANCE.getMyUnitsByFilter(unit -> unit.getType() == UnitType.WORKER).length;
		numFactories = 0;
		numBuiltFactories = 0;
		numRockets = 0;
		numBuiltRockets = 0;
		for (Unit unit : structures) {
			if (unit.getType() == UnitType.FACTORY) {
				numFactories++;
				if (unit.isStructureBuilt()) {
					numBuiltFactories++;
				}
			}
			if (unit.getType() == UnitType.ROCKET) {
				numRockets++;
				if (unit.isStructureBuilt()) {
					numBuiltRockets++;
				}
			}
		}
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (unit.getLocation().getMapLocation().equals(location)) {
			Direction direction = getBlueprintDirection(location);
			UnitType type = getBlueprintType();
			if (!unit.hasWorkerActed() && unit.canBlueprint(type, direction)) {
				unit.blueprint(type, direction);
			}
		}
	}
	
	@Override
	public boolean isStopCondition(MapLocation location) {
		if ((numFactories - numBuiltFactories) + (numRockets - numBuiltRockets) >= 2) {
			return false; // Complete your structures before building others!
		}
		if (getBlueprintDirection(location) == null) {
			return false;
		}
		UnitType toBlueprint = getBlueprintType();
		if (toBlueprint == null) return false;
		int karbonite = GameController.INSTANCE.getCurrentKarbonite();
		switch(toBlueprint) {
			case FACTORY:
				return karbonite >= Constants.FACTORY_COST;
			case ROCKET:
				return karbonite >= Constants.ROCKET_COST;
			default:
				return false;
		}
	}
}
