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
	private MapLocation stopLocation;
	private Direction stopDirection;
	public BFS(MapLocation destination) {
		this.data = new Direction[destination.getPlanet().getWidth()][destination.getPlanet().getHeight()];
		this.queue = new ArrayDeque<MapLocation>();
		queue.add(destination);
	}
	public Direction trace(Vector source, Vector destination) {
		Vector step = source.add(data[source.getX()][source.getY()].getOffsetVector());
		if(destination.equals(step)) {
			return data[source.getX()][source.getY()];
		}else {
			return trace(step, destination);
		}
	}
	public Direction getDirection(Vector position) {
		return data[position.getX()][position.getY()];
	}
	public void process(Predicate<MapLocation> passable) {
		process(passable, x->false);
	}
	@SafeVarargs
	public final <T extends Predicate<MapLocation>> T process(Predicate<MapLocation> passable, T... stopConditions) {
		while(!queue.isEmpty()) {
			MapLocation polled = queue.poll();
			for(Direction direction: Direction.values()) {
				MapLocation step = polled.getOffsetLocation(direction);
				if(step.isOnMap()) {
					if(passable.test(step)&&data[step.getPosition().getX()][step.getPosition().getY()]==null) {
						data[step.getPosition().getX()][step.getPosition().getY()] = direction.getOpposite();
						queue.add(step);
					}
					for(T stopCondition: stopConditions) {
						if(stopCondition.test(step)) {
							stopLocation = polled;
							stopDirection = direction;
							return stopCondition;
						}
					}
				}
			}
		}
		return null;
	}
	public Deque<MapLocation> getQueue(){
		return queue;
	}
	public MapLocation getStopLocation() {
		return stopLocation;
	}
	public Direction getStopDirection() {
		return stopDirection;
	}
}
