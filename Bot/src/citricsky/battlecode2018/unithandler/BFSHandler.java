package citricsky.battlecode2018.unithandler;

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
	private long cumulativeTime;

	public BFSHandler(Unit unit, Predicate<MapLocation> passablePredicate, Set<MapLocation> occupied, PathfinderTask... pathfinderTasks) {
		this.unit = unit;
		this.passablePredicate = passablePredicate;
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
		if (bfs.getStopLocation() != null) {
			int bestPriority = -Util.getMovementDistance(bfs.getStopLocation().getPosition(), mapLocation.getPosition());
			if (bestPriority <= priority) {
				return Integer.MIN_VALUE;
			}
		}
		long time = System.currentTimeMillis();
		task = bfs.process(passablePredicate, pathfinderTasks);
		time = System.currentTimeMillis() - time;
		cumulativeTime += time;
		if (bfs.getStopLocation() == null) {
			return Integer.MIN_VALUE;
		}
		if(time > 10) {
			System.out.println("BFS Time: "+time+"/"+cumulativeTime+"ms - "+task.getClass().getSimpleName()+" w/ "+unit.getId());
		}
		return -Util.getMovementDistance(bfs.getStopLocation().getPosition(), mapLocation.getPosition());
	}
	@Override
	public void execute() {
		//System.out.println("Executing BFSHandler: "+unit.getType().toString()+" - "+unit.getLocation().getMapLocation().getPosition()+" - "+bfs.getStopLocation().getPosition());
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
		cumulativeTime += time;
		if(time > 10) {
			System.out.println("Execution Time: "+time+"/"+cumulativeTime+"ms - "+task.getClass().getSimpleName()+" w/ "+unit.getId());
		}
	}
}