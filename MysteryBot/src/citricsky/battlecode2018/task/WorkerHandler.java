package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;

public class WorkerHandler {
	private static Unit getBuildTarget(MapLocation location) {
		Unit bestTarget = null;
		double highestHealth = -Double.MAX_VALUE;
		
		for(Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					Unit unit = offset.getUnit();
					if(unit.getType().isStructure() && (!unit.isStructureBuilt()) &&
							unit.getTeam() == GameController.INSTANCE.getTeam()) {
						double health = ((double)unit.getHealth()) / ((double)unit.getMaxHealth());
						if(health > highestHealth) {
							highestHealth = health;
							bestTarget = unit;
						}
					}
				}
			}
		}
		return bestTarget;
	}
	private static Unit getRepairTarget(MapLocation location) {
		Unit bestTarget = null;
		double lowestHealth = Double.MAX_VALUE;
		
		for(Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (GameController.INSTANCE.canSenseLocation(offset)) {
				if (offset.hasUnitAtLocation()) {
					Unit unit = offset.getUnit();
					if(unit.getType().isStructure() && unit.isStructureBuilt() &&
							unit.getTeam() == GameController.INSTANCE.getTeam() && 
							unit.getHealth() < unit.getMaxHealth()) {
						double health = ((double)unit.getHealth()) / ((double)unit.getMaxHealth());
						if(health < lowestHealth) {
							lowestHealth = health;
							bestTarget = unit;
						}
					}
				}
			}
		}
		return bestTarget;
	}
	public boolean doWorkerAction(Unit unit) {
		workerAction: 
		if (!unit.hasWorkerActed()) {
			//try build
			Unit buildTarget = getBuildTarget(unit.getLocation().getMapLocation());
			if(buildTarget != null) {
				if (unit.canBuild(buildTarget)) {
					unit.build(buildTarget);
					break workerAction;
				}
			}
			//try repair
			Unit repairTarget = getRepairTarget(unit.getLocation().getMapLocation());
			if (repairTarget != null) {
				if (unit.canRepair(repairTarget)) {
					unit.repair(repairTarget);
					break workerAction;
				}
			}
			//try blueprint
			UnitType blueprintType = getBlueprintType();
			if (blueprintType != null &&
					moveManager.getBFSStep(MoveManager.BFS_WORKER_BLUEPRINT, unit.getLocation().getMapLocation().getPosition()) == BFS.SOURCE_STEP) {
				Direction blueprintDirection = null;
				Vector position = null;
				int bestBuild = -1;
				for (Direction direction: Direction.COMPASS) {
					MapLocation location = unit.getLocation().getMapLocation().getOffsetLocation(direction);
					if (unit.canBlueprint(blueprintType, direction)) {
						if (isNextToStructure(location)) {
							continue;
						}
						int buildArray = moveManager.getBlueprint(location.getPosition().getX(), location.getPosition().getY());
						if (buildArray > bestBuild) {
							position = location.getPosition();
							bestBuild = buildArray;
							blueprintDirection = direction;
						}
					}
				}
				if (blueprintDirection != null) {
					unit.blueprint(blueprintType, blueprintDirection);
					RoundInfo.addStructure(position.getX(), position.getY());
					break workerAction;
				}
			}
			//try harvest
			for (Direction direction: Direction.values()) {
				if (unit.canHarvest(direction)) {
					unit.harvest(direction);
					break workerAction;
				}
			}
		}
	}
}
