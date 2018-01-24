import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

public class BFS {
	private Predicate<Vector> passable;
	
	private static final int DIRECTION_SHIFT = 0;
	private static final int DIRECTION_BITMASK = 0b1111;
	private static final int SOURCE_SHIFT = 4;
	private static final int SOURCE_BITMASK = 0b111111111111; //12 bits = 4096, max = 50 * 50 = 2500
	private static final int STEP_SHIFT = 16;
	private static final int STEP_BITMASK = 0b111111111111; //12 bits = 4096, max = 50 * 50 = 2500
	
	public static final int SOURCE_STEP = 0b1;
	
	private int[][] data;
	
	private Deque<Vector> queue;
	private int step;
	
	public BFS(int width, int height, Predicate<Vector> passable, Vector... sources) {
		this.data = new int[width][height];
		this.passable = passable;
		this.queue = new ArrayDeque<Vector>();
		this.step = SOURCE_STEP + 1;
		for (int i = 0; i < sources.length; ++i) {
			addSource(sources[i], i);
		}
	}
	public void addSource(Vector source, int sourceId) {
		if(outOfBounds(source)) {
			return;
		}
		if((data[source.getX()][source.getY()] >>> STEP_SHIFT) != SOURCE_STEP) {
			queue.add(source);
			data[source.getX()][source.getY()] =
					((Direction.CENTER.ordinal() & DIRECTION_BITMASK) << DIRECTION_SHIFT) |
					((sourceId & SOURCE_BITMASK) << SOURCE_SHIFT) |
					((SOURCE_STEP & STEP_BITMASK) << STEP_SHIFT);
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
	public int getSourceId(int x, int y) {
		if (getStep(x, y) == Integer.MAX_VALUE) {
			return -1;
		}
		return (data[x][y] >>> SOURCE_SHIFT) & SOURCE_BITMASK;
	}
	public Direction getDirectionToSource(int x, int y) {
		if (getStep(x, y) == Integer.MAX_VALUE) {
			return null;
		}
		return Direction.values()[(data[x][y] >>> DIRECTION_SHIFT) & DIRECTION_BITMASK];
	}
	public int getStep(int x, int y) {
		if (outOfBounds(x, y)) {
			return Integer.MAX_VALUE;
		}
		int step = (data[x][y] >>> STEP_SHIFT) & STEP_BITMASK;
		if (step == 0) {
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
			int sourceId = (data[vector.getX()][vector.getY()] >>> SOURCE_SHIFT) & SOURCE_BITMASK;
			for(Direction direction: Direction.COMPASS) {
				Vector candidate = vector.add(direction.getOffsetVector());
				if((!outOfBounds(candidate)) && passable.test(candidate)) {
					if (data[candidate.getX()][candidate.getY()] == 0) {
						data[candidate.getX()][candidate.getY()] =
								((direction.getOpposite().ordinal() & DIRECTION_BITMASK) << DIRECTION_SHIFT) |
								((sourceId & SOURCE_BITMASK) << SOURCE_SHIFT) |
								((step & STEP_BITMASK) << STEP_SHIFT);
						queue.add(candidate);
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
