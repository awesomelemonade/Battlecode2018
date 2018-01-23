package citricsky.battlecode2018.main;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
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
	private static final int BFS_LOAD_ROCKET = 5;
	private BFS[] bfsArray;
	private Planet planet;
	
	public MoveManager() {
		this.planet = GameController.INSTANCE.getPlanet();
		//Initialize bfsArray
	}
	public void update() {
		for (BFS bfs: bfsArray) {
			bfs.resetHard();
		}
		for (Unit unit: RoundInfo.getEnemiesOnMap()) {
			Vector position = unit.getLocation().getMapLocation().getPosition();
			bfsArray[BFS_FIND_ENEMY].addSource(position);
			addSource(BFS_KNIGHT_ATTACK, position, Direction.COMPASS);
			for (int i = 0; i < RANGER_OFFSET_X.length; ++i) {
				bfsArray[BFS_RANGER_ATTACK].addSource(position.add(RANGER_OFFSET_X[i], RANGER_OFFSET_Y[i]));
			}
		}
		for (Unit unit: RoundInfo.getMyUnits()) {
			if (unit.getLocation().isOnMap()) {
				Vector position = unit.getLocation().getMapLocation().getPosition();
				if (unit.isStructure() && unit.getHealth() < unit.getMaxHealth()) {
					addSource(BFS_WORKER, position, Direction.COMPASS);
				}
				if (unit.getType() == UnitType.ROCKET && unit.isStructureBuilt() &&
						unit.getGarrisonUnitIds().length < unit.getStructureMaxCapacity()) {
					addSource(BFS_LOAD_ROCKET, position, Direction.COMPASS);
				}
				if (unit.getType() == UnitType.HEALER) {
					bfsArray[BFS_FIND_HEAL].addSource(position); //simplicity sake, you could probably precompute offsets later
				}
			}
		}
		//change so it only updates on harvest, rather than every turn
		for (int i = 0; i < planet.getWidth(); ++i) {
			for (int j=0; j < planet.getHeight(); ++j) {
				MapLocation location = planet.getMapLocation(i, j);
				if (GameController.INSTANCE.canSenseLocation(location)) {
					if (location.getKarboniteCount() > 0) {
						bfsArray[BFS_WORKER].addSource(location.getPosition());
					}
				} else {
					if (planet.getStartingMap().getInitialKarboniteAt(location) > 0) {
						bfsArray[BFS_WORKER].addSource(location.getPosition());
					}
				}
			}
		}
	}
	public void addSource(int index, Vector position, Direction[] directions) {
		for (Direction direction: directions) {
			bfsArray[index].addSource(position.add(direction.getOffsetVector()));
		}
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
	public void processBFS(int index) {
		while (!bfsArray[index].getQueue().isEmpty()) {
			bfsArray[index].step();
		}
	}
}
