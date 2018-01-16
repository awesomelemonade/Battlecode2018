package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;
import citricsky.battlecode2018.util.Constants;
import citricsky.battlecode2018.util.Util;

import java.util.function.Predicate;

public class WorkerBlueprintRocketTask implements PathfinderTask {
	private Unit[] structures;
	private int numFactories;
	private int numRockets;
	private int numBuiltRockets;
	private int karbonite;
	private boolean locked;

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
	private final Predicate<MapLocation> stopCondition = location -> {
		if (numRockets >= numFactories) {
			return false;
		}
		if (numRockets > numBuiltRockets) {
			return false;
		}
		if (locked) {
			return false;
		}
		if (karbonite < Constants.ROCKET_COST) {
			return false;
		}
		return getBlueprintDirection(location) != null;
	};

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
		karbonite = GameController.INSTANCE.getCurrentKarbonite();
		locked = GameController.INSTANCE.getResearchInfo().getQueue().length >= 8;
	}

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
	public void execute(Unit unit, MapLocation location) {
		if (unit.getLocation().getMapLocation().equals(location)) {
			Direction direction = getBlueprintDirection(location);
			if (!unit.hasWorkerActed() && unit.canBlueprint(UnitType.ROCKET, direction)) {
				unit.blueprint(UnitType.ROCKET, direction);
				karbonite = GameController.INSTANCE.getCurrentKarbonite();
				numRockets++;
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}
