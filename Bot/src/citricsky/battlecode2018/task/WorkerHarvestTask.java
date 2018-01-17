package citricsky.battlecode2018.task;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class WorkerHarvestTask implements PathfinderTask {
	private final Predicate<MapLocation> stopCondition = location -> getHarvestDirection(location) != null;
	private Direction getHarvestDirection(MapLocation location) {
		for(Direction direction: Direction.values()) {
			MapLocation offset = location.getOffsetLocation(direction);
			if(!offset.isOnMap()) {
				continue;
			}
			boolean notAvailable = false;
			for (Unit enemyUnit: enemyUnits) {
				if (enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(offset.getPosition())<=enemyUnit.getVisionRange()) {
					notAvailable = true;
					break;
				}
			}
			if(notAvailable) {
				continue;
			}
			if(GameController.INSTANCE.canSenseLocation(offset)) {
				if(offset.getKarboniteCount() > 0) {
					return direction;
				}
			}else {
				if(offset.getPlanet().getStartingMap().getInitialKarboniteAt(offset) > 0) {
					return direction;
				}
			}
		}
		return null;
	}
	private Unit[] enemyUnits;
	@Override
	public void update() {
		this.enemyUnits = GameController.INSTANCE.getAllUnitsByFilter(unit -> unit.getLocation().isOnMap() &&
				unit.getTeam() == GameController.INSTANCE.getEnemyTeam());
	}
	@Override
	public void execute(Unit unit, MapLocation location) {
		if(unit.getLocation().getMapLocation().equals(location)) {
			if(!unit.hasWorkerActed()) {
				Direction direction = getHarvestDirection(location);
				if(unit.canHarvest(direction)) {
					unit.harvest(direction);
				}
			}
		}
	}
	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}
