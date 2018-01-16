package citricsky.battlecode2018.main;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Vector;

public class BFS {
	private int[][] data;
	private Set<MapLocation> queue;
	private MapLocation source;
	private Set<MapLocation> stopLocations;
	private boolean checkedSource;

	public BFS(MapLocation source) {
		this.source = source;
		this.data = new int[source.getPlanet().getWidth()][source.getPlanet().getHeight()];
		this.queue = new HashSet<MapLocation>();
		this.stopLocations = new HashSet<MapLocation>();
		queue.add(source);
		this.checkedSource = false;
	}

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
		stopLocations.clear();
		if (!checkedSource) {
			for (T stopCondition : stopConditions) {
				if (stopCondition.test(source)) {
					this.stopLocations.add(source);
					return stopCondition;
				}
			}
			checkedSource = true;
		}
		do {
			Set<MapLocation> toAdd = new HashSet<MapLocation>();
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
			for(MapLocation location : toAdd) {
				data[location.getPosition().getX()][location.getPosition().getY()] = data[location.getPosition().getX()][location.getPosition().getY()] | 1;
				queue.add(location);
			}
			for(MapLocation location : toAdd) {
				for (T stopCondition : stopConditions) {
					if (stopCondition.test(location)) {
						this.stopLocations.add(location);
					}
				}
			}
		} while (!queue.isEmpty());
		return null;
	}

	public Set<MapLocation> getQueueSet() {
		return queue;
	}

	public MapLocation getSource() {
		return source;
	}

	public Set<MapLocation> getStopLocations() {
		return stopLocations;
	}
}
