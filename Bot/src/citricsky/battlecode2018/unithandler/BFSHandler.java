package citricsky.battlecode2018.unithandler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.main.BFS;

public class BFSHandler implements UnitHandler {
	private Unit unit;
	private BFS bfs;
	private Set<MapLocation> occupied;
	private PathfinderTask[] pathfinderTasks;
	private PathfinderTask task;
	private MapLocation stopLocation;
	private Planet planet;

	public BFSHandler(Unit unit, Predicate<MapLocation> passablePredicate, Set<MapLocation> occupied, PathfinderTask... pathfinderTasks) {
		this.unit = unit;
		this.occupied = occupied;
		Set<PathfinderTask> tasks = new HashSet<PathfinderTask>();
		for(PathfinderTask task: pathfinderTasks) {
			if(task.isActivated(unit)) {
				tasks.add(task);
			}
		}
		this.pathfinderTasks = tasks.toArray(new PathfinderTask[tasks.size()]);
		if (unit.getLocation().isOnMap()) {
			MapLocation source = unit.getLocation().getMapLocation();
			this.planet = source.getPlanet();
			this.bfs = new BFS(planet.getWidth(), planet.getHeight(),
					vector -> {
						MapLocation location = planet.getMapLocation(vector);
						return passablePredicate.test(location) && (!occupied.contains(location));
					}, source.getPosition());
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
		bfs.reset();
		for (;bfs.getCurrentStep()-2 < -priority && (!bfs.getQueue().isEmpty()); bfs.step()) {
			for (Vector vector: bfs.getQueue()) {
				MapLocation location = planet.getMapLocation(vector);
				for (PathfinderTask pathfinderTask: pathfinderTasks) {
					if (pathfinderTask.test(location)) {
						this.task = pathfinderTask;
						this.stopLocation = location;
						return -(bfs.getCurrentStep()-2);
					}
				}
			}
		}
		return Integer.MIN_VALUE;
	}
	@Override
	public void execute() {
		if (!unit.getLocation().getMapLocation().equals(stopLocation)) {
			if(unit.isMoveReady()) {
				int directions = bfs.getDirectionFromSource(stopLocation.getPosition().getX(), stopLocation.getPosition().getY());
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
		occupied.add(stopLocation);
		long time = System.currentTimeMillis();
		task.execute(unit, stopLocation);
		time = System.currentTimeMillis() - time;
		if(time > 10) {
			System.out.println("Execution Time: "+time+"ms - "+task.getClass().getSimpleName()+" w/ "+unit.getId());
		}
	}
}