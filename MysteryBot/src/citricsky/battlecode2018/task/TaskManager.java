package citricsky.battlecode2018.task;

import java.util.Comparator;
import java.util.PriorityQueue;

import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.util.Constants;

public class TaskManager {
	private PriorityQueue<Unit> queue;
	private int[] priorities;
	public TaskManager() {
		this.priorities = new int[Constants.MAX_UNIT_ID];
		queue = new PriorityQueue<Unit>(7, new Comparator<Unit>() {
			@Override
			public int compare(Unit a, Unit b) {
				return Integer.compare(priorities[b.getId()], priorities[a.getId()]);
			}
		});
	}
	public void execute() {
		queue.clear();
		
	}
}
