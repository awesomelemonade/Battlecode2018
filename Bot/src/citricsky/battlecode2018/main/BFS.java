package citricsky.battlecode2018.main;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Vector;

public class BFS {
	private Direction[][] data;
	private Deque<MapLocation> queue;
	private MapLocation source;
	private MapLocation stopLocation;
	public BFS(MapLocation source) {
		this.source = source;
		this.data = new Direction[source.getPlanet().getWidth()][source.getPlanet().getHeight()];
		this.queue = new ArrayDeque<MapLocation>();
		queue.add(source);
	}
	public Direction getDirectionFromSource(Vector vector) {
		Vector step = vector.add(data[vector.getX()][vector.getY()].getOffsetVector());
		if(source.getPosition().equals(step)) {
			return data[vector.getX()][vector.getY()].getOpposite();
		}else {
			return getDirectionFromSource(step);
		}
	}
	public Direction getDirectionToSource(Vector vector) {
		return data[vector.getX()][vector.getY()];
	}
	public Direction getDirection(Vector position) {
		return data[position.getX()][position.getY()];
	}
	public void process(Predicate<MapLocation> passable) {
		process(passable, x -> false);
	}
	@SafeVarargs
	public final <T extends Predicate<MapLocation>> T process(Predicate<MapLocation> passable, T... stopConditions) {
		this.stopLocation = null;
		for(T stopCondition: stopConditions) {
			if(stopCondition.test(source)) {
				this.stopLocation = source;
				return stopCondition;
			}
		}
		while(!queue.isEmpty()) {
			MapLocation polled = queue.poll();
			for(T stopCondition: stopConditions) {
				if(stopCondition.test(polled)) {
					queue.addFirst(polled);
					this.stopLocation = polled;
					return stopCondition;
				}
			}
			for(Direction direction: Direction.COMPASS) {
				MapLocation step = polled.getOffsetLocation(direction);
				if(step.equals(source)) {
					continue;
				}
				if(step.isOnMap()) {
					if(passable.test(step) && data[step.getPosition().getX()][step.getPosition().getY()] == null) {
						data[step.getPosition().getX()][step.getPosition().getY()] = direction.getOpposite();
						queue.add(step);
					}
				}
			}
		}
		return null;
	}
	public Deque<MapLocation> getQueue(){
		return queue;
	}
	public MapLocation getSource() {
		return source;
	}
	public MapLocation getStopLocation() {
		return stopLocation;
	}
}
