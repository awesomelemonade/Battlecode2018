package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.main.MoveManager;
import citricsky.battlecode2018.main.RoundInfo;
import citricsky.battlecode2018.util.Constants;
import citricsky.battlecode2018.util.Util;

public class WorkerExecutor implements UnitExecutor {
	private MoveManager moveManager;
	public WorkerExecutor(MoveManager moveManager) {
		this.moveManager = moveManager;
	}
	private static Unit getBuildTarget(MapLocation location) {
		Unit bestTarget = null;
		double highestHealth = -Double.MAX_VALUE;
		
		for(Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if(GameController.INSTANCE.canSenseLocation(offset)) {
				if(offset.hasUnitAtLocation()) {
					Unit unit = offset.getUnit();
					if(unit.getType().isStructure() && (!unit.isStructureBuilt()) &&
							unit.getTeam() == GameController.INSTANCE.getTeam()) {
						double health = ((double)unit.getHealth())/((double)unit.getMaxHealth());
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
			if(GameController.INSTANCE.canSenseLocation(offset)) {
				if(offset.hasUnitAtLocation()) {
					Unit unit = offset.getUnit();
					if(unit.getType().isStructure() && unit.isStructureBuilt() &&
							unit.getTeam() == GameController.INSTANCE.getTeam() && 
							unit.getHealth() < unit.getMaxHealth()) {
						double health = ((double)unit.getHealth())/((double)unit.getMaxHealth());
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
	private boolean shouldReplicate() {
		return GameController.INSTANCE.getCurrentKarbonite() > Constants.WORKER_REPLICATE_COST && RoundInfo.getRoundNumber() > 3 && 
				RoundInfo.getUnitCount(UnitType.WORKER) * 2 - 6 < RoundInfo.getUnitCount(UnitType.FACTORY);
	}
	public UnitType getBlueprintType() {
		if (RoundInfo.getMyUnits().length > 60) {
			return UnitType.ROCKET;
		}
		if (RoundInfo.getRoundNumber() < 100 || RoundInfo.getUnitCount(UnitType.FACTORY) < 3 || RoundInfo.getMyUnits().length < 20) {
			return UnitType.FACTORY;
		}else {
			return UnitType.ROCKET;
		}
	}
	@Override
	public void execute(Unit unit) {
		if (shouldReplicate()) {
			Direction bestReplicateDirection = null;
			int closestTask = Integer.MAX_VALUE;
			for (Direction direction: Direction.COMPASS) {
				if (unit.canReplicate(direction)) {
					Vector position = unit.getLocation().getMapLocation().getPosition().add(direction.getOffsetVector());
					int bfsStep = Math.min(moveManager.getBFSStep(MoveManager.BFS_WORKER_HARVEST, position) + 3, 
							moveManager.getBFSStep(MoveManager.BFS_WORKER_TASK, position)) - 1;
					if (bfsStep < closestTask) {
						closestTask = bfsStep;
						bestReplicateDirection = direction;
					}
				}
			}
			if (bestReplicateDirection != null) {
				unit.replicate(bestReplicateDirection);
			}
		}
		//try build
		Unit buildTarget = getBuildTarget(unit.getLocation().getMapLocation());
		if(buildTarget != null) {
			if (unit.canBuild(buildTarget)) {
				unit.build(buildTarget);
				return;
			}
		}
		//try repair
		Unit repairTarget = getRepairTarget(unit.getLocation().getMapLocation());
		if (repairTarget != null) {
			if (unit.canRepair(repairTarget)) {
				unit.repair(repairTarget);
				return;
			}
		}
		//try blueprint
		UnitType blueprintType = getBlueprintType();
		if (blueprintType == UnitType.ROCKET ||
				(RoundInfo.getUnitCount(UnitType.FACTORY) <
						(RoundInfo.getRoundNumber() < 50 ? 1 : (RoundInfo.getRoundNumber() < 100 ? 3 : 5)))) {
			Direction blueprintDirection = null;
			int bestBuild = -1;
			for (Direction direction: Direction.COMPASS) {
				MapLocation location = unit.getLocation().getMapLocation().getOffsetLocation(direction);
				if (unit.canBlueprint(blueprintType, direction)) {
					if (isNextToStructure(location)) {
						continue;
					}
					int buildArray = Util.getBuildArray(Util.getNeighbors(location, Util.PASSABLE_PREDICATE.negate()));
					if (buildArray > bestBuild) {
						bestBuild = buildArray;
						blueprintDirection = direction;
					}
				}
			}
			if (blueprintDirection != null) {
				unit.blueprint(blueprintType, blueprintDirection);
				return;
			}
		}
		//try harvest
		for (Direction direction: Direction.values()) {
			if (unit.canHarvest(direction)) {
				unit.harvest(direction);
				return;
			}
		}
	}
	private boolean isNextToStructure(MapLocation location) {
		for (Direction dir: Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(dir);
			if (offset.hasUnitAtLocation()) {
				if (offset.getUnit().getTeam() == GameController.INSTANCE.getTeam()) {
					if (offset.getUnit().getType().isStructure()) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
