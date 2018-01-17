package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class WorkerRepairTask implements PathfinderTask {
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

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (unit.getLocation().getMapLocation().equals(location)) {
			if (!unit.hasWorkerActed()) {
				Unit repairTarget = WorkerRepairTask.getRepairTarget(location);
				if (unit.canRepair(repairTarget)) {
					unit.repair(repairTarget);
				}
			}
		}
	}
	
	@Override
	public boolean isStopCondition(MapLocation location) {
		return WorkerRepairTask.getRepairTarget(location) != null;
	}
}
