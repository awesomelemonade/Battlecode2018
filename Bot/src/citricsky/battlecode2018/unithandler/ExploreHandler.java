package citricsky.battlecode2018.unithandler;

import java.util.HashSet;
import java.util.Set;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.main.BFS;
import citricsky.battlecode2018.util.Util;

public class ExploreHandler implements UnitHandler {
	private static Set<Vector> visited;
	private Unit unit;
	static {
		visited = new HashSet<Vector>();
	}
	public ExploreHandler(Unit unit) {
		this.unit = unit;
	}
	@Override
	public void execute() {
		if (!unit.getLocation().isOnMap()) return;
		if (!unit.isMoveReady()) return;
		
		MapLocation source = unit.getLocation().getMapLocation();
		
		BFS bfs = new BFS(source.getPlanet().getWidth(), source.getPlanet().getHeight(),
				vector -> Util.PASSABLE_PREDICATE.test(source.getPlanet().getMapLocation(vector)), source.getPosition());
		
		MapLocation location = processBFS(bfs, source.getPlanet());
		
		if (location != null) {
			int directions = bfs.getDirectionFromSource(location.getPosition().getX(), location.getPosition().getY());
			for(Direction direction: Direction.COMPASS) {
				if(((directions >>> direction.ordinal()) & 1) == 1) {
					if(unit.canMove(direction)) {
						unit.move(direction);
						break;
					}
				}
			}
		}else {
			Direction direction = Direction.randomDirection();
			if(unit.canMove(direction)) {
				unit.move(direction);
			}
		}
	}
	public MapLocation processBFS(BFS bfs, Planet planet) {
		for (;(!bfs.getQueue().isEmpty()) && bfs.getCurrentStep() < 20; bfs.step()) {
			for (Vector vector: bfs.getQueue()) {
				if(visited.contains(vector)) {
					continue;
				}
				MapLocation location = planet.getMapLocation(vector);
				if (GameController.INSTANCE.canSenseLocation(location)) {
					visited.add(vector);
				} else {
					return location;
				}
			}
		}
		return null;
	}
	@Override
	public int getPriority(int priority) {
		return -Integer.MAX_VALUE + 1;
	}
}
