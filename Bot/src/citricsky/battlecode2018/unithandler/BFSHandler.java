package citricsky.battlecode2018.unithandler;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.main.BFS;

public class BFSHandler implements UnitHandler {
	private Unit unit;
	private BFS bfs;
	private PathfinderTask[] pathfinderTasks;
	private PathfinderTask task;
	
	public BFSHandler(Unit unit, PathfinderTask... pathfinderTasks) {
		this.unit = unit;
		this.pathfinderTasks = pathfinderTasks;
		if(unit.getLocation().isOnMap()) {
			this.bfs = new BFS(unit.getLocation().getMapLocation());
		}
	}
	
	@Override
	public int getPriority(int priority) {
		if (!unit.getLocation().isOnMap()) {
			return Integer.MIN_VALUE;
		}
		MapLocation mapLocation = unit.getLocation().getMapLocation();
		if(bfs.getStopLocation() != null) {
			int bestPriority = -bfs.getStopLocation().getPosition().getDistanceSquared(mapLocation.getPosition());
			if(bestPriority<=priority) {
				return Integer.MIN_VALUE;
			}
		}
		task = bfs.process(location -> location.isPassableTerrain(), pathfinderTasks);
		if (bfs.getStopLocation() == null) {
			return Integer.MIN_VALUE;
		}
		return -bfs.getStopLocation().getPosition().getDistanceSquared(mapLocation.getPosition());
	}
	@Override
	public void execute() {
		if (!unit.getLocation().getMapLocation().equals(bfs.getStopLocation())) {
			Direction direction = bfs.getDirectionFromSource(bfs.getStopLocation().getPosition());
			if (unit.isMoveReady() && unit.canMove(direction)) {
				unit.move(direction);
			}
		}
		task.execute(unit, bfs.getStopLocation());
	}
}