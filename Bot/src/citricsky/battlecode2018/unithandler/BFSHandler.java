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
	}
	@Override
	public int getPriority(int priority) {
		if(!unit.getLocation().isOnMap()) {
			return -Integer.MAX_VALUE;
		}
		MapLocation mapLocation = unit.getLocation().getMapLocation();
		this.bfs = new BFS(mapLocation);
		task = bfs.process(location -> {
			return location.isPassableTerrain();
		}, pathfinderTasks);
		return bfs.getQueue().peekLast().getPosition().getDistanceSquared(mapLocation.getPosition());
	}
	@Override
	public void execute() {
		if(!unit.getLocation().getMapLocation().equals(bfs.getSource())) {
			Direction direction = bfs.getDirectionFromSource(bfs.getStopLocation().getPosition());
			if(unit.isMoveReady() && unit.canMove(direction)) {
				unit.move(direction);
			}
		}
		task.execute(unit, bfs.getStopLocation());
	}
}