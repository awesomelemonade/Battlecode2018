package citricsky.battlecode2018.main;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.Vector;

public class BFSDestination {
	private Direction[][] data;
	private Deque<Vector> queue;
	public BFSDestination(int width, int height, Vector destination) {
		this.data = new Direction[width][height];
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
	public void process(Predicate<Vector> passable) {
		process(passable, x->false);
	}
	public void process(Predicate<Vector> passable, Predicate<Vector> stopCondition) {
		while(!queue.isEmpty()) {
			Vector polled = queue.poll();
			for(Direction direction: Direction.values()) {
				Vector step = polled.add(direction.getOffsetVector());
				if(passable.test(step)&&data[step.getX()][step.getY()]==null) {
					data[step.getX()][step.getY()] = direction.getOpposite();
					queue.add(step);
					if(stopCondition.test(step)) {
						return;
					}
				}
			}
		}
	}
	public Deque<Vector> getQueue(){
		return queue;
	}
}
