package citricsky.battlecode2018.main;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;

public class MoveManager {
	private BFS findHealBfs;
	private BFS workerBfs;
	private BFS knightAttackBfs;
	public void update() {
		for (Unit unit: RoundInfo.getEnemiesOnMap()) {
			for (Direction direction: Direction.COMPASS) {
				knightAttackBfs.addSource(unit.getLocation().getMapLocation().getPosition().add(direction.getOffsetVector()));
			}
		}
		while (!knightAttackBfs.getQueue().isEmpty()) {
			knightAttackBfs.step();
		}
		//add build to workerBfs
		//add repair to workerBfs
		//add harvest to workerBfs
	}
	public void move() {
		Unit[] units = GameController.INSTANCE.getMyUnitsByFilter(unit -> unit.getLocation().isOnMap());
		Map<Integer, Integer> priorities = new HashMap<Integer, Integer>();
		PriorityQueue<Unit> queue = new PriorityQueue<Unit>(units.length, new Comparator<Unit>() {
			@Override
			public int compare(Unit a, Unit b) {
				return Integer.compare(priorities.get(a.getId()), priorities.get(b.getId()));
			}
		});
		for (Unit unit: units) {
			priorities.put(unit.getId(), getPriority(unit));
			queue.add(unit);
		}
		while (!queue.isEmpty()) {
			move(queue.poll());
		}
	}
	public int getPriority(Unit unit) {
		return Integer.MIN_VALUE;
	}
	public void move(Unit unit) {
		
	}
}
