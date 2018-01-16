package citricsky.battlecode2018.main;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Vector;

public class BFS {
	private int[][] data;
	//private PriorityQueue<MapLocation> queue;
	private Set<MapLocation> queue;
	private Set<MapLocation> toAdd;
	private MapLocation source;
	private MapLocation stopLocation;
	private boolean checkedSource;

	public BFS(MapLocation source) {
		this.source = source;
		this.data = new int[source.getPlanet().getWidth()][source.getPlanet().getHeight()];
		/*this.queue = new PriorityQueue<MapLocation>(7, new Comparator<MapLocation>() {
			@Override
			public int compare(MapLocation a, MapLocation b) { //movement is not circular, it's taxi geometry with weird diagonals
				return getMovementDistanceFromSource(a.getPosition())-getMovementDistanceFromSource(b.getPosition());
			}
		});*/
		this.queue = new HashSet<MapLocation>();
		toAdd = new HashSet<MapLocation>();
		this.stopLocation = null;
		queue.add(source);
		this.checkedSource = false;
	}
	
	/*private int getMovementDistanceFromSource(Vector position) {
		return Math.max(Math.abs(position.getX()-source.getPosition().getX()), Math.abs(position.getY()-source.getPosition().getY()));
	}*/

	public int getDirectionFromSource(Vector vector) {
		int info = data[vector.getX()][vector.getY()];
		if (source.getPosition().equals(vector)) {
			return ((info >>> 5) & 0b00001111) | ((info << 3) & 0b11110000);
		}
		int returnValue = 0;
		for(Direction direction: Direction.COMPASS) {
			if (((info >>> (direction.ordinal() + 1)) & 1) == 1) {
				returnValue = returnValue | getDirectionFromSource(vector.add(direction.getOffsetVector()));
			}
		}
		return returnValue;
	}

	public int getDirectionToSource(Vector vector) {
		return (data[vector.getX()][vector.getY()] >>> 1) & 0b11111111;
	}

	public void process(Predicate<MapLocation> passable) {
		process(passable, x -> false);
	}

	@SafeVarargs
	public final <T extends Predicate<MapLocation>> T process(Predicate<MapLocation> passable, T... stopConditions) {
		this.stopLocation = null;
		if (!checkedSource) {
			for (T stopCondition : stopConditions) {
				if (stopCondition.test(source)) {
					this.stopLocation = source;
					return stopCondition;
				}
			}
			checkedSource = true;
		}
		while((!queue.isEmpty()) || (!toAdd.isEmpty())) {
			Set<MapLocation> adding = new HashSet<MapLocation>(toAdd);
			for(MapLocation location: adding) {
				toAdd.remove(location);
				for (T stopCondition : stopConditions) {
					if (stopCondition.test(location)) {
						this.stopLocation = location;
						return stopCondition;
					}
				}
			}
			for(MapLocation location : queue) {
				for (Direction direction : Direction.COMPASS) {
					MapLocation step = location.getOffsetLocation(direction);
					if(step.isOnMap()) {
						if(passable.test(step) && (data[step.getPosition().getX()][step.getPosition().getY()] & 1) == 0) {
							data[step.getPosition().getX()][step.getPosition().getY()] =
									data[step.getPosition().getX()][step.getPosition().getY()] | (1 << (direction.getOpposite().ordinal()+1));
							toAdd.add(step);
						}
					}
				}
			}
			queue.clear();
			for(MapLocation location: toAdd) {
				data[location.getPosition().getX()][location.getPosition().getY()] = data[location.getPosition().getX()][location.getPosition().getY()] | 1;
			}
		}
		return null;
	}

	public Set<MapLocation> getQueueSet() {
		return queue;
	}

	public MapLocation getSource() {
		return source;
	}

	public MapLocation getStopLocation() {
		return stopLocation;
	}
}
