package citricsky.battlecode2018.unithandler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

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
	private Predicate<MapLocation> passablePredicate;

	public BFSHandler(Unit unit, Predicate<MapLocation> passablePredicate, Set<MapLocation> occupied, PathfinderTask... pathfinderTasks) {
		this.unit = unit;
		this.passablePredicate = passablePredicate;
		this.occupied = occupied;
		Set<PathfinderTask> tasks = new HashSet<PathfinderTask>();
		for(PathfinderTask task: pathfinderTasks) {
			if(task.isActivated(unit)) {
				tasks.add(task);
			}
		}
		this.pathfinderTasks = tasks.toArray(new PathfinderTask[tasks.size()]);
		if (unit.getLocation().isOnMap()) {
			this.bfs = new BFS(unit.getLocation().getMapLocation());
		}
	}

	@Override
	public int getPriority(int priority) {
		if (!unit.getLocation().isOnMap()) {
			return Integer.MIN_VALUE;
		}
		if(pathfinderTasks.length == 0) {
			return Integer.MIN_VALUE;
		}
		MapLocation mapLocation = unit.getLocation().getMapLocation();
		if (bfs.getStopLocation() != null) {
			int bestPriority = -Util.getMovementDistance(bfs.getStopLocation().getPosition(), mapLocation.getPosition());
			if (bestPriority <= priority) {
				return Integer.MIN_VALUE;
			}
		}
		task = bfs.process(passablePredicate, pathfinderTasks);
		if (bfs.getStopLocation() == null) {
			return Integer.MIN_VALUE;
		}
		return -Util.getMovementDistance(bfs.getStopLocation().getPosition(), mapLocation.getPosition());
	}
	@Override
	public void execute() {
		if (!unit.getLocation().getMapLocation().equals(bfs.getStopLocation())) {
			if(unit.isMoveReady()) {
				int directions = bfs.getDirectionFromSource(bfs.getStopLocation().getPosition());
				for(Direction direction: Direction.COMPASS) {
					if(((directions >>> direction.ordinal()) & 1) == 1) {
						if(unit.canMove(direction)) {
							unit.move(direction);
							break;
						}
					}
				}
			}
		}
		occupied.add(bfs.getStopLocation());
		long time = System.currentTimeMillis();
		task.execute(unit, bfs.getStopLocation());
		time = System.currentTimeMillis() - time;
		if(time > 10) {
			System.out.println("Execution Time: "+time+"ms - "+task.getClass().getSimpleName()+" w/ "+unit.getId());
		}
	}
}