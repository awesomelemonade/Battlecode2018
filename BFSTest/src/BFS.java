import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class BFS {
	private Predicate<Vector> passable;
	//Least significant bit = Stores whether it is a source
	//2nd least significant bit = Stores whether it has been visited by BFS
	//3rd-10th least significant bits = Stores the direction from source
	//11th-18th least significant bits = Stores direction to source
	private int[][] data;
	
	private Set<Vector> queue;
	private int step;
	
	public BFS(Vector... sources) {
		for(Vector source: sources) {
			queue.add(source);
			data[source.getX()][source.getY()] = 0b11;
		}
		this.queue = new HashSet<Vector>();
		this.step = 0;
	}
	public int getDirectionFromSource(Vector vector) {
		return (data[vector.getX()][vector.getY()] >>> 2) & 0b11111111;
	}
	public int getDirectionToSource(Vector vector) {
		return (data[vector.getX()][vector.getY()] >>> 10) & 0b11111111;
	}
	public boolean outOfBounds(Vector vector) {
		return vector.getX() < 0 || vector.getY() < 0 || vector.getX() >= data.length || vector.getY() >= data[0].length;
	}
	public void step() {
		Set<Vector> toAdd = new HashSet<Vector>();
		for(Vector vector: queue) {
			for(Direction direction: Direction.COMPASS) {
				Vector candidate = vector.add(direction.getOffsetVector());
				if((!outOfBounds(candidate)) && passable.test(candidate) && (((data[candidate.getX()][candidate.getY()] >>> 1) & 1) == 0)) {
					if((data[vector.getX()][vector.getY()] & 1) == 1) { //checks whether vector is source
						data[candidate.getX()][candidate.getY()] |= (1 << (direction.ordinal()+2)); 
					}else {
						data[candidate.getX()][candidate.getY()] |= (data[vector.getX()][vector.getY()] & 0b1111111100);
					}
					data[candidate.getX()][candidate.getY()] |= (1 << (direction.getOpposite().ordinal() + 10)); //direction to source
					toAdd.add(candidate);
				}
			}
		}
		for(Vector vector: toAdd) {
			data[vector.getX()][vector.getY()] |= 0b10; // Set to visited
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
