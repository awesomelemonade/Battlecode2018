package citricsky.battlecode2018.unithandler;

import java.util.Set;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.main.BFS;
import citricsky.battlecode2018.util.Util;

public class BFSHandler implements UnitHandler {
	private Unit unit;
	private BFS bfs;
	private Set<MapLocation> occupied;
	private PathfinderTask[] pathfinderTasks;
	private PathfinderTask task;

	public BFSHandler(Unit unit, Set<MapLocation> occupied, PathfinderTask... pathfinderTasks) {
		this.unit = unit;
		this.occupied = occupied;
		this.pathfinderTasks = pathfinderTasks;
		if (unit.getLocation().isOnMap()) {
			this.bfs = new BFS(unit.getLocation().getMapLocation());
		}
	}

	@Override
	public int getPriority(int priority) {
		if (!unit.getLocation().isOnMap()) {
			return Integer.MIN_VALUE;
		}
		MapLocation mapLocation = unit.getLocation().getMapLocation();
		if (!bfs.getStopLocations().isEmpty()) {
			int bestPriority = -bfs.getStopLocations().getPosition().getDistanceSquared(mapLocation.getPosition());
			if (bestPriority <= priority) {
				return Integer.MIN_VALUE;
			}
		}
		long time = System.currentTimeMillis();
		task = bfs.process(Util.PASSABLE_PREDICATE, pathfinderTasks);
		time = System.currentTimeMillis()-time;
		if (bfs.getStopLocation() == null) {
			return Integer.MIN_VALUE;
		}
		if(time>10) {
			System.out.println("BFS Time: "+time+"ms"+" - "+task.getClass().getSimpleName());
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
		occupied.add(bfs.getStopLocation());
		task.execute(unit, bfs.getStopLocation());
	}
}