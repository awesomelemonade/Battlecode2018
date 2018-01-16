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
	private Unit[] structures;
	private int numFactories;
	private int numRockets;
	private int numBuiltRockets;

	private Predicate<MapLocation> passablePredicate = location -> {
		if (!location.isOnMap()) {
			return false;
		}
		if (location.hasUnitAtLocation()) {
			if (location.getUnit().getTeam() == GameController.INSTANCE.getTeam()) {
				if (location.getUnit().isStructure()) {
					return false;
				}
			}
		}
		return location.isPassableTerrain();
	};
	private Predicate<MapLocation> stopCon = location -> {
		if (numRockets > numBuiltRockets) return false;
		if (numFactories > numRockets + 1) return false;
		return GameController.INSTANCE.getCurrentKarbonite() >= Constants.FACTORY_COST && getBlueprintDirection(location) != null;
	};

	private Direction getBlueprintDirection(MapLocation location) {
		for (Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (!passablePredicate.test(offset)) {
				continue;
			}
			if (Util.canBuild(Util.getNeighbors(location.getOffsetLocation(direction), passablePredicate))) {
				return direction;
			}
		}
		return null;
	}

	@Override
	public void update() {
		structures = GameController.INSTANCE.getMyUnitsByFilter(Unit::isStructure);
		numFactories = 0;
		numRockets = 0;
		numBuiltRockets = 0;
		for (Unit unit : structures) {
			if (unit.getType() == UnitType.FACTORY) numFactories++;
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
			if (!unit.hasWorkerActed() && unit.canBlueprint(UnitType.FACTORY, direction)) {
				unit.blueprint(UnitType.FACTORY, direction);
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCon;
	}
}
