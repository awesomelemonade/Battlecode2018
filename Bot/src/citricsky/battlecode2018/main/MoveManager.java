package citricsky.battlecode2018.main;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.Vector;

public class MoveManager {
	// 40 to 50 attack range for Ranger
	private static final int[] RANGER_OFFSET_X = new int[] { 7, 7, 6, 6, 5, 5, 4, 3, 2, 1, 0, -1, -2, -3, -4, -5, -5, -6, -6,
			-7, -7, -7, -6, -6, -5, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 5, 6, 6, 7 };
	private static final int[] RANGER_OFFSET_Y = new int[] { 0, 1, 2, 3, 4, 5, 5, 6, 6, 7, 7, 7, 6, 6, 5, 5, 4, 3, 2, 1, 0, -1,
			-2, -3, -4, -5, -5, -6, -6, -7, -7, -7, -6, -6, -5, -5, -4, -3, -2, -1 };
	private static final int BFS_FIND_ENEMY = 0;
	private static final int BFS_FIND_HEAL = 1;
	private static final int BFS_WORKER = 2;
	private static final int BFS_KNIGHT_ATTACK = 3;
	private static final int BFS_RANGER_ATTACK = 4;
	private BFS[] bfsArray;
	public void update() {
		for (Unit unit: RoundInfo.getEnemiesOnMap()) {
			Vector position = unit.getLocation().getMapLocation().getPosition();
			bfsArray[BFS_FIND_ENEMY].addSource(position);
			for (Direction direction: Direction.COMPASS) {
				bfsArray[BFS_KNIGHT_ATTACK].addSource(position.add(direction.getOffsetVector()));
			}
			for (int i = 0; i < RANGER_OFFSET_X.length; ++i) {
				bfsArray[BFS_RANGER_ATTACK].addSource(position.add(RANGER_OFFSET_X[i], RANGER_OFFSET_Y[i]));
			}
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
