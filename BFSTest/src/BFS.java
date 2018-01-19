import java.util.Deque;
import java.util.function.Predicate;

public class BFS {
	private Predicate<Vector> passable;
	private int[][] data;
	
	private Deque<Vector> queue;
	private int step;
	
	public BFS(Vector... sources) {
		for(Vector source: sources) {
			queue.add(source);
			data[source.getX()][source.getY()]  = 1;
		}
		this.step = 0;
	}
	
	public void step() {
		
		step++;
	}
	public int getCurrentStep() {
		return step;
	}
}
