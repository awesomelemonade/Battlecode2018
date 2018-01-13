package citricsky.battlecode2018.main;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.Vector;

public class BFSDestination {
	private PlanMap planMap;
	private Direction[][] data;
	private Deque<Vector> queue;
	public BFSDestination(Vector destination) {
		this.data = new Direction[planMap.getWidth()][planMap.getHeight()];
		this.queue = new ArrayDeque<Vector>();
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
	public void process() {
		process(x->false);
	}
	public void process(Predicate<Vector> predicate) {
		while(!queue.isEmpty()) {
			Vector polled = queue.poll();
			for(Direction direction: Direction.values()) {
				Vector step = polled.add(direction.getOffsetVector());
				if(planMap.isPassable(step)&&data[step.getX()][step.getY()]==null) {
					data[step.getX()][step.getY()] = direction.getOpposite();
					queue.add(step);
					if(predicate.test(step)) {
						break;
					}
				}
			}
		}
	}
	public Deque<Vector> getQueue(){
		return queue;
	}
}
