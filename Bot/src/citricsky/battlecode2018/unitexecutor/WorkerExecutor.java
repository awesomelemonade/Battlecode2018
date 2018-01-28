package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.main.BFS;
import citricsky.battlecode2018.main.MoveManager;
import citricsky.battlecode2018.main.RoundInfo;
import citricsky.battlecode2018.util.Constants;

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
	private boolean shouldReplicate(Unit unit) {
		if (GameController.INSTANCE.getCurrentKarbonite() > Constants.WORKER_REPLICATE_COST) {
			if (RoundInfo.getRoundNumber() < 20) {
				return true;
			}
			if (RoundInfo.getRoundNumber() > 750) {
				return true;
			}
			if (GameController.INSTANCE.getPlanet() == Planet.EARTH) {
				if (moveManager.getBFSStep(MoveManager.BFS_WORKER_BLUEPRINT, unit.getLocation().getMapLocation().getPosition()) == Integer.MAX_VALUE) {
					return true;
				}
			}
			int minKarbonite = 60;
			if (RoundInfo.getRoundNumber() < 50) {
				minKarbonite = 20;
			}
			if (RoundInfo.getRoundNumber() < 100) {
				minKarbonite = 30;
			}
			if (RoundInfo.getRoundNumber() < 150) {
				MapLocation unitLocation = unit.getLocation().getMapLocation();
				for(Direction offset : Direction.COMPASS) {
					if(unitLocation.getOffsetLocation(offset).getKarboniteCount()>=minKarbonite) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public static UnitType getBlueprintType() {
		UnitType target = getBlueprintTargetType();
		if (target.getBaseCost() <= GameController.INSTANCE.getCurrentKarbonite()) {
			return target;
		} else {
			return null;
		}
	}
	public static UnitType getBlueprintTargetType() {
		if (RoundInfo.getCombatUnitsCount() > 50) {
			return UnitType.ROCKET;
		}
		if ((RoundInfo.getRoundNumber() < 100) ||
				(RoundInfo.getUnitCount(UnitType.FACTORY) < 5 && RoundInfo.getRoundNumber() < 625) ||
					(RoundInfo.getUnitCount(UnitType.FACTORY) < 1 && RoundInfo.getRoundNumber() < 675)) {
			return UnitType.FACTORY;
		} else {
			return UnitType.ROCKET;
		}
	}
	@Override
	public void execute(Unit unit) {
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
		if (shouldReplicate(unit)) {
			Direction bestReplicateDirection = null;
			Vector replicatePosition = null;
			int closestTask = Integer.MAX_VALUE;
			for (Direction direction: Direction.COMPASS) {
				if (unit.canReplicate(direction)) {
					Vector position = unit.getLocation().getMapLocation().getPosition().add(direction.getOffsetVector());
					int bfsIndex = moveManager.getBFSIndex(UnitType.WORKER, unit.getLocation().getMapLocation().getPlanet(), position, 1.0);
					int bfsStep = moveManager.getBFSStep(bfsIndex, position);
					if (bfsStep < closestTask) {
						closestTask = bfsStep;
						replicatePosition = position;
						bestReplicateDirection = direction;
					}
				}
			}
			if (bestReplicateDirection != null) {
				unit.replicate(bestReplicateDirection);
				MapLocation location = GameController.INSTANCE.getPlanet().getMapLocation(replicatePosition);
				moveManager.queueUnit(location.getUnit()); // throw the new unit into the queue
			}
		}
	}
	public boolean isNextToStructure(MapLocation location) {
		for (Direction dir: Direction.COMPASS) {
			Vector offset = location.getPosition().add(dir.getOffsetVector());
			if (RoundInfo.hasStructure(offset.getX(), offset.getY())) {
				return true;
			}
		}
		return false;
	}
}
