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

public class WorkerExecutor implements UnitExecutor {
	private MoveManager moveManager;
	public WorkerExecutor(MoveManager moveManager) {
		this.moveManager = moveManager;
	}
	@Override
	public void update() {
		
	}
	private static Unit getBuildTarget(MapLocation location) {
		Unit bestTarget = null;
		double highestHealth = -Double.MAX_VALUE;
		
		for(Direction direction : Direction.COMPASS) {
			MapLocation offset = location.getOffsetLocation(direction);
			if(GameController.INSTANCE.canSenseLocation(offset)) {
				if(offset.hasUnitAtLocation()) {
					Unit unit = offset.getUnit();
					if(unit.isStructure() && (!unit.isStructureBuilt()) &&
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
					if(unit.isStructure() && unit.isStructureBuilt() &&
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
		return GameController.INSTANCE.getCurrentKarbonite() > Constants.WORKER_REPLICATE_COST && 
				RoundInfo.getRoundNumber() > 3;
	}
	public UnitType getBlueprintType() {
		if(RoundInfo.getRoundNumber() < 500 || RoundInfo.getUnitCount(UnitType.FACTORY) < 2) {
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
					int bfsStep = moveManager.getBFSStep(MoveManager.BFS_WORKER, position);
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
		for (Direction direction: Direction.COMPASS) {
			UnitType type = getBlueprintType();
			if (unit.canBlueprint(type, direction)) {
				unit.blueprint(type, direction);
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
}
