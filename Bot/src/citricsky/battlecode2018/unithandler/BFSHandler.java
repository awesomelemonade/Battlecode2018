package citricsky.battlecode2018.unithandler;

import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.main.BFSDestination;

public class BFSHandler implements UnitHandler {
	private Unit unit;
	private BFSDestination bfs;
	private PathfinderTask[] pathfinderTasks;
	private PathfinderTask task;
	public BFSHandler(Unit unit, PathfinderTask... pathfinderTasks) {
		this.unit = unit;
		this.pathfinderTasks = pathfinderTasks;
	}
	@Override
	public int getPriority(int priority) {
		if(!unit.getLocation().isOnMap()) {
			return -Integer.MAX_VALUE;
		}
		MapLocation mapLocation = unit.getLocation().getMapLocation();
		this.bfs = new BFSDestination(mapLocation);
		task = bfs.process(location -> {
			return location.getPlanet().getStartingMap().isPassableTerrainAt(location);
		}, pathfinderTasks);
		return bfs.getQueue().peekLast().getPosition().getDistanceSquared(mapLocation.getPosition());
	}
	@Override
	public void execute() {
		task.execute();
	}
}