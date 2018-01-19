import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class BFS {
	private Predicate<Vector> passable;
	//Least significant bit = Stores whether it is a source
	//2nd least significant bit = Stores whether it has been visited by BFS
	//3rd-10th least significant bits = Stores the direction from source
	//11th-18th least significant bits = Stores direction to source
	//19th-32nd bits = 16384 max
	
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
	
	private Set<Vector> queue;
	private int step;
	
	public BFS(int width, int height, Predicate<Vector> passable, Vector... sources) {
		this.data = new int[width][height];
		this.passable = passable;
		this.queue = new HashSet<Vector>();
		this.step = SOURCE_STEP;
		for(Vector source: sources) {
			queue.add(source);
			data[source.getX()][source.getY()] = (SOURCE_STEP << STEP_SHIFT); //set step = 1
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
	public boolean outOfBounds(Vector vector) {
		return vector.getX() < 0 || vector.getY() < 0 || vector.getX() >= data.length || vector.getY() >= data[0].length;
	}
	public void step() {
		Set<Vector> toAdd = new HashSet<Vector>();
		for(Vector vector: queue) {
			for(Direction direction: Direction.COMPASS) {
				Vector candidate = vector.add(direction.getOffsetVector());
				if((!outOfBounds(candidate)) && passable.test(candidate) && (((data[candidate.getX()][candidate.getY()] >>> STEP_SHIFT) & STEP_BITMASK) == 0)) {
					if(((data[vector.getX()][vector.getY()] >>> STEP_SHIFT) & STEP_BITMASK) == SOURCE_STEP) { //checks whether vector is source
						data[candidate.getX()][candidate.getY()] |= (1 << (direction.ordinal() + FROM_SHIFT)); 
					}else {
						data[candidate.getX()][candidate.getY()] |= (data[vector.getX()][vector.getY()] & (DIRECTION_BITMASK << FROM_SHIFT));
					}
					data[candidate.getX()][candidate.getY()] |= (1 << (direction.getOpposite().ordinal() + TO_SHIFT)); //direction to source
					toAdd.add(candidate);
				}
			}
		}
		queue.clear();
		for(Vector vector: toAdd) {
			queue.add(vector);
		}
		step++;
	}
	public Set<Vector> getQueue(){
		return queue;
	}
	public int getCurrentStep() {
		return step;
	}
}
