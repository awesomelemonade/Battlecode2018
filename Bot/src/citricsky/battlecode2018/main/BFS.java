package citricsky.battlecode2018.main;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.util.Benchmark;

public class BFS {
	private int[][] data;
	//private PriorityQueue<MapLocation> queue;
	private Benchmark benchmark;
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
		this.benchmark = new Benchmark();
		this.queue = new HashSet<MapLocation>();
		this.toAdd = new HashSet<MapLocation>();
		this.stopLocation = null;
		queue.add(source);
		this.checkedSource = false;
	}

	public int getDirectionFromSource(Vector vector) {
		int info = data[vector.getX()][vector.getY()];
		int returnValue = 0;
		for(Direction direction: Direction.COMPASS) {
			if (((info >>> (direction.ordinal() + 1)) & 1) == 1) {
				Vector offset = vector.add(direction.getOffsetVector());
				if(offset.equals(source.getPosition())) {
					returnValue = returnValue | ((info >>> 5) & 0b00001111) | ((info << 3) & 0b11110000);
				}else {
					returnValue = returnValue | getDirectionFromSource(vector.add(direction.getOffsetVector()));
				}
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
	
	public <T extends Predicate<MapLocation>> T checkStopConditions(T[] stopConditions, long[] cumulative, MapLocation location){
		for(int i = 0; i < stopConditions.length; ++i) {
			benchmark.push();
			boolean test = stopConditions[i].test(location);
			cumulative[i] += benchmark.pop();
			if(test) {
				return stopConditions[i];
			}
		}
		return null;
	}
	
	@SafeVarargs
	public final <T extends Predicate<MapLocation>> T process(Predicate<MapLocation> passable, T... stopConditions) {
		long[] cumulative = new long[stopConditions.length];
		T toReturn = null;
		this.stopLocation = null;
		if (!checkedSource) {
			toReturn = checkStopConditions(stopConditions, cumulative, source);
			if(toReturn == null) {
				checkedSource = true;
			} else {
				this.stopLocation = source;
			}
		}
		mainLoop: while(((!queue.isEmpty()) || (!toAdd.isEmpty())) && stopLocation == null) {
			Set<MapLocation> adding = new HashSet<MapLocation>(toAdd);
			for(MapLocation location: adding) {
				toAdd.remove(location);
				queue.add(location);
				toReturn = checkStopConditions(stopConditions, cumulative, location);
				if (toReturn != null) {
					this.stopLocation = location;
					break mainLoop;
				}
			}
			for(MapLocation location : queue) {
				for (Direction direction : Direction.COMPASS) {
					MapLocation step = location.getOffsetLocation(direction);
					if(step.isOnMap()) {
						if(!step.equals(source)) {
							if(passable.test(step) && (data[step.getPosition().getX()][step.getPosition().getY()] & 1) == 0) {
								data[step.getPosition().getX()][step.getPosition().getY()] =
										data[step.getPosition().getX()][step.getPosition().getY()] | (1 << (direction.getOpposite().ordinal()+1));
								toAdd.add(step);
							}
						}
					}
				}
			}
			queue.clear();
			for(MapLocation location: toAdd) {
				data[location.getPosition().getX()][location.getPosition().getY()] = data[location.getPosition().getX()][location.getPosition().getY()] | 1;
			}
		}
		for(int i = 0; i < cumulative.length; ++i) {
			if(cumulative[i] > 10000000) {
				System.out.println("BFS Process: " + stopConditions[i].getClass().getSimpleName() +
						" - " + (cumulative[i] / 1000000) + "ms");
			}
		}
		return toReturn;
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
