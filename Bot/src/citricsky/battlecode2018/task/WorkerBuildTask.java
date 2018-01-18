package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class WorkerBuildTask implements PathfinderTask {
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

	@Override
	public void execute(Unit unit, MapLocation location) {
		if (unit.getLocation().getMapLocation().equals(location)) {
			if (!unit.hasWorkerActed()) {
				Unit buildTarget = WorkerBuildTask.getBuildTarget(location);
				if (unit.canBuild(buildTarget)) {
					unit.build(buildTarget);
				}
			}
		}
	}
	
	@Override
	public boolean isStopCondition(MapLocation location) {
		return WorkerBuildTask.getBuildTarget(location) != null;
	}
}
