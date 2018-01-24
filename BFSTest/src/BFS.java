import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

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
	public static final int SOURCE_STEP = 0b1;
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
		if((data[source.getX()][source.getY()] >>> STEP_SHIFT) != SOURCE_STEP) {
			queue.add(source);
			data[source.getX()][source.getY()] = (SOURCE_STEP << STEP_SHIFT);
		}
	}
	public void reset() {
		this.step = SOURCE_STEP + 1;
		queue.clear();
		for (int i = 0;i < getWidth(); ++i) {
			for (int j = 0;j < getHeight(); ++j) {
				if (((data[i][j] >>> STEP_SHIFT) & STEP_BITMASK) == SOURCE_STEP) {
					queue.add(new Vector(i, j));
				} else {
					data[i][j] = 0;
				}
			}
		}
	}
	public void resetHard() {
		this.step = SOURCE_STEP + 1;
		queue.clear();
		for (int i = 0; i < this.getWidth(); ++i) {
			for (int j = 0; j < this.getHeight(); ++j) {
				data[i][j] = 0;
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
		if(outOfBounds(x, y)) {
			return 0;
		}
		return (data[x][y] >>> FROM_SHIFT) & DIRECTION_BITMASK;
	}
	public int getDirectionToSource(int x, int y) {
		if(outOfBounds(x, y)) {
			return 0;
		}
		return (data[x][y] >>> TO_SHIFT) & DIRECTION_BITMASK;
	}
	public int getStep(int x, int y) {
		if(outOfBounds(x, y)) {
			return Integer.MAX_VALUE;
		}
		int step = (data[x][y] >>> STEP_SHIFT) & STEP_BITMASK;
		if(step == 0) {
			return Integer.MAX_VALUE;
		} else {
			return step;
		}
	}
	public boolean outOfBounds(Vector vector) {
		return outOfBounds(vector.getX(), vector.getY());
	}
	public boolean outOfBounds(int x, int y) {
		return x < 0 || y < 0 || x >= data.length || y >= data[0].length;
	}
	public void step() {
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
	}
	public Deque<Vector> getQueue(){
		return queue;
	}
	public int getCurrentStep() {
		return step;
	}
}
