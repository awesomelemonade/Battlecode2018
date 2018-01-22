package citricsky.battlecode2018.main;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.util.Benchmark;

public class BFS {
	private Predicate<Vector> passable;
	//Least significant bit = Stores whether it has been visited
	//2nd-9th least significant bits = stores direction from source
	//10th-17th least significant bits = stores direction to source
	//18th-32nd bits = max = 32768 = step number
	private static final int FROM_SHIFT = 0;
	private static final int TO_SHIFT = 8;
	private static final int STEP_SHIFT = 16;
	private static final int DIRECTION_BITMASK = 0b11111111;
	private static final int STEP_BITMASK = 0b1111111111111111;
	private static final int SOURCE_STEP = 0b1;
	private int[][] data;
	
	private Deque<Vector> queue;
	private int step;
	
	public BFS(int width, int height, Predicate<Vector> passable, Vector... sources) {
		this.data = new int[width][height];
		this.passable = passable;
		this.queue = new ArrayDeque<Vector>();
		this.step = SOURCE_STEP + 1;
		for(Vector source: sources) {
			addSource(source);
		}
	}
	public void addSource(Vector source) {
		if(outOfBounds(source)) {
			return;
		}
		queue.add(source);
		data[source.getX()][source.getY()] = (SOURCE_STEP << STEP_SHIFT);
	}
	public void reset() {
		this.step = SOURCE_STEP + 1;
		queue.clear();
		for (int i=0;i<getWidth();++i) {
			for (int j=0;j<getHeight();++j) {
				if (((data[i][j] >>> STEP_SHIFT) & STEP_BITMASK) == SOURCE_STEP) {
					queue.add(new Vector(i, j));
				} else {
					data[i][j] = 0;
				}
			}
		}
	}
	public int getWidth() {
		return data.length;
	}
	public int getHeight() {
		return data[0].length;
	}
	public int getDirectionFromSource(int x, int y) {
		return (data[x][y] >>> FROM_SHIFT) & DIRECTION_BITMASK;
	}
	public int getDirectionToSource(int x, int y) {
		return (data[x][y] >>> TO_SHIFT) & DIRECTION_BITMASK;
	}
	public int getStep(int x, int y) {
		return (data[x][y] >>> STEP_SHIFT) & STEP_BITMASK;
	}
	public boolean outOfBounds(Vector vector) {
		return vector.getX() < 0 || vector.getY() < 0 || vector.getX() >= data.length || vector.getY() >= data[0].length;
	}
	public void step() {
		Benchmark benchmark = new Benchmark();
		benchmark.push();
		for(int i = 0, size = queue.size(); i < size; ++i) {
			Vector vector = queue.poll();
			for(Direction direction: Direction.COMPASS) {
				Vector candidate = vector.add(direction.getOffsetVector());
				if((!outOfBounds(candidate)) && passable.test(candidate)) {
					int currentStep = (data[candidate.getX()][candidate.getY()] >>> STEP_SHIFT) & STEP_BITMASK;
					if(currentStep == 0) { //check whether step has been set
						data[candidate.getX()][candidate.getY()] |= (step << STEP_SHIFT);
						queue.add(candidate);
					}
					if(currentStep == 0 || currentStep == step) {
						if(((data[vector.getX()][vector.getY()] >>> STEP_SHIFT) & STEP_BITMASK) == SOURCE_STEP) { //checks whether vector is source
							data[candidate.getX()][candidate.getY()] |= (1 << (direction.ordinal() + FROM_SHIFT)); 
						}else {
							data[candidate.getX()][candidate.getY()] |= (data[vector.getX()][vector.getY()] & (DIRECTION_BITMASK << FROM_SHIFT));
						}
						data[candidate.getX()][candidate.getY()] |= (1 << (direction.getOpposite().ordinal() + TO_SHIFT)); //direction to source
					}
				}
			}
		}
		step++;
		double deltaTime = benchmark.pop() / 1000000.0;
		if (deltaTime > 10) {
			System.out.println("Step: " + deltaTime + "ms");
		}
	}
	public Deque<Vector> getQueue(){
		return queue;
	}
	public int getCurrentStep() {
		return step;
	}
}
