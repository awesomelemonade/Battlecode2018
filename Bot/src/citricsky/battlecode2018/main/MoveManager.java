package citricsky.battlecode2018.main;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Consumer;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.util.Util;

public class MoveManager {
	// 40 to 50 attack range for Ranger
	private static final int[] RANGER_OFFSET_X = new int[] { 7, 7, 6, 6, 5, 5, 4, 3, 2, 1, 0, -1, -2, -3, -4, -5, -5, -6, -6,
			-7, -7, -7, -6, -6, -5, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 5, 6, 6, 7 };
	private static final int[] RANGER_OFFSET_Y = new int[] { 0, 1, 2, 3, 4, 5, 5, 6, 6, 7, 7, 7, 6, 6, 5, 5, 4, 3, 2, 1, 0, -1,
			-2, -3, -4, -5, -5, -6, -6, -7, -7, -7, -6, -6, -5, -5, -4, -3, -2, -1 };
	private static final int[] HEALER_OFFSET_X = new int[] {
			0, 1, 2, 3, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1, 0, -1, -2, -3, -4, -5, -5, -5, -5, -5, -4, -3, -2, -1
	};
	private static final int[] HEALER_OFFSET_Y = new int[] {
			5, 5, 5, 4, 3, 2, 1, 0, -1, -2, -3, -4, -5, -5, -5, -5, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 5
	};
	public static final int BFS_FIND_ENEMY = 0;
	public static final int BFS_FIND_HEAL = 1;
	public static final int BFS_WORKER_TASK = 2;
	public static final int BFS_WORKER_HARVEST = 3;
	public static final int BFS_KNIGHT_ATTACK = 4;
	public static final int BFS_RANGER_ATTACK = 5;
	public static final int BFS_HEALER_HEAL = 6;
	public static final int BFS_LOAD_ROCKET = 7;
	public static final int BFS_EXPLORE = 8;
	private BFS[] bfsArray;
	private boolean[] processed;
	private Planet planet;
	
	public MoveManager() {
		this.planet = GameController.INSTANCE.getPlanet();
		//Initialize bfsArray
		this.bfsArray = new BFS[9];
		this.processed = new boolean[bfsArray.length];
		for (int i = 0; i < bfsArray.length; ++i) {
			bfsArray[i] = new BFS(planet.getWidth(), planet.getHeight(),
					vector -> Util.PASSABLE_PREDICATE.test(planet.getMapLocation(vector)));
		}
	}
	public void updateBFS() {
		for (int i = 0; i < bfsArray.length; ++i) {
			bfsArray[i].resetHard();
			processed[i] = false;
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
				if (unit.getHealth() < unit.getMaxHealth()) {
					if (unit.isStructure()) {
						addSource(BFS_WORKER_TASK, position, Direction.COMPASS);
					} else {
						for (int i = 0; i < HEALER_OFFSET_X.length; ++i) {
							Vector offset = position.add(HEALER_OFFSET_X[i], HEALER_OFFSET_Y[i]);
							if (getBFSStep(BFS_FIND_ENEMY, offset) < 12) {
								continue;
							}
							bfsArray[BFS_HEALER_HEAL].addSource(offset);
						}
					}
				}
				if (unit.getType() == UnitType.ROCKET && unit.isStructureBuilt() &&
						unit.getGarrisonUnitIds().length < unit.getStructureMaxCapacity()) {
					bfsArray[BFS_LOAD_ROCKET].addSource(position);
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
				if (getBFSStep(BFS_FIND_ENEMY, location.getPosition()) < 12) {
					continue;
				}
				if (GameController.INSTANCE.canSenseLocation(location)) {
					if (location.getKarboniteCount() > 0) {
						bfsArray[BFS_WORKER_HARVEST].addSource(location.getPosition());
					}
				} else {
					bfsArray[BFS_EXPLORE].addSource(location.getPosition());
					if (planet.getStartingMap().getInitialKarboniteAt(location) > 0) {
						bfsArray[BFS_WORKER_HARVEST].addSource(location.getPosition());
					}
				}
				//If location can be blueprint a factory... make sure you check the karbonite count before putting in the BFS TODO
			}
		}
	}
	public void addSource(int index, Vector position, Direction[] directions) {
		for (Direction direction: directions) {
			bfsArray[index].addSource(position.add(direction.getOffsetVector()));
		}
	}
	public void move(Consumer<Unit> executor) {
		Unit[] units = GameController.INSTANCE.getMyUnitsByFilter(
				unit -> unit.getLocation().isOnMap());
		if (units.length > 0) {
			Map<Integer, Integer> priorities = new HashMap<Integer, Integer>();
			Map<Integer, Integer> bfsIndices = new HashMap<Integer, Integer>();
			PriorityQueue<Unit> queue = new PriorityQueue<Unit>(units.length, new Comparator<Unit>() {
				@Override
				public int compare(Unit a, Unit b) {
					return Integer.compare(priorities.get(a.getId()), priorities.get(b.getId()));
				}
			});
			for (Unit unit: units) {
				Vector position = unit.getLocation().getMapLocation().getPosition();
				if (unit.isStructure()) {
					priorities.put(unit.getId(), Integer.MIN_VALUE);
				} else {
					int bfsIndex = getBFSIndex(unit, position);
					bfsIndices.put(unit.getId(), bfsIndex);
					if (bfsIndex == BFS_EXPLORE) {
						priorities.put(unit.getId(), Integer.MIN_VALUE);
					} else {
						priorities.put(unit.getId(), -bfsArray[bfsIndex].getStep(position.getX(), position.getY()));
					}
				}
				queue.add(unit);
			}
			while (!queue.isEmpty()) {
				try {
					Unit unit = queue.poll();
					if ((!unit.isStructure()) && unit.isMoveReady()) {
						if (unit.getType() == UnitType.RANGER && unit.isRangerSniping()) {
							continue;
						}
						MapLocation location = unit.getLocation().getMapLocation();
						int bfsIndex = bfsIndices.get(unit.getId());
						int step = getBFSStep(bfsIndex, location.getPosition());
						if (step == Integer.MAX_VALUE) {
							Direction random = Direction.randomDirection();
							if (unit.canMove(random)) {
								unit.move(random);
							}
						} else if (step == BFS.SOURCE_STEP) {
							for (Direction direction: Direction.COMPASS) {
								MapLocation offset = location.getOffsetLocation(direction);
								if (Util.PASSABLE_PREDICATE.test(offset) && getBFSStep(bfsIndex, offset.getPosition()) == BFS.SOURCE_STEP) {
									if (unit.canMove(direction)) {
										unit.move(direction);
										break;
									}
								}
							}
						} else {
							Direction direction = getBFSDirection(bfsIndex, location.getPosition());
							if (unit.canMove(direction)) {
								unit.move(direction);
							}
						}
					}
					executor.accept(unit);
				} catch (Exception ex) {
					System.out.println("Move Exception: "+ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}
	public int getBFSIndex(Unit unit, Vector position) {
		if (unit.getHealth() < unit.getMaxHealth() / 2 && unit.getType() != UnitType.HEALER) {
			if (getBFSStep(BFS_FIND_HEAL, position) != Integer.MAX_VALUE) {
				return BFS_FIND_HEAL;
			}
		}
		if (unit.getType() == UnitType.WORKER) {
			int workerTaskStep = getBFSStep(BFS_WORKER_TASK, position);
			int workerHarvestStep = getBFSStep(BFS_WORKER_HARVEST, position);
			if (workerTaskStep - 3 <= workerHarvestStep) {
				return BFS_WORKER_TASK;
			} else {
				return BFS_WORKER_HARVEST;
			}
		}
		if (unit.getLocation().getMapLocation().getPlanet() == Planet.EARTH) {
			int loadRocketStep = getBFSStep(BFS_LOAD_ROCKET, position);
			if (loadRocketStep < 10 || RoundInfo.getRoundNumber() > 600) {
				return BFS_LOAD_ROCKET;
			}
		}
		int bfsAttackIndex = -1;
		if (unit.getType() == UnitType.KNIGHT) {
			bfsAttackIndex = BFS_KNIGHT_ATTACK;
		}
		if (unit.getType() == UnitType.RANGER) {
			bfsAttackIndex = BFS_RANGER_ATTACK;
		}
		if (unit.getType() == UnitType.HEALER) {
			bfsAttackIndex = BFS_HEALER_HEAL;
		}
		if (bfsAttackIndex == -1) {
			bfsAttackIndex = BFS_FIND_ENEMY;
		}
		int attackStep = getBFSStep(bfsAttackIndex, position);
		if (attackStep < 10) {
			return bfsAttackIndex;
		}else {
			if (bfsAttackIndex != BFS_FIND_ENEMY) {
				int findStep = getBFSStep(BFS_FIND_ENEMY, position);
				if (findStep != Integer.MAX_VALUE) {
					return BFS_FIND_ENEMY;
				}
			}
		}
		return BFS_EXPLORE;
	}
	public Direction getBFSDirection(int bfsIndex, Vector position) {
		if (!processed[bfsIndex]) {
			processBFS(bfsIndex);
		}
		return bfsArray[bfsIndex].getDirectionToSource(position.getX(), position.getY());
	}
	public int getBFSStep(int bfsIndex, Vector position) {
		if (!processed[bfsIndex]) {
			processBFS(bfsIndex);
		}
		return bfsArray[bfsIndex].getStep(position.getX(), position.getY());
	}
	public void processBFS(int index) {
		while (!bfsArray[index].getQueue().isEmpty()) {
			bfsArray[index].step();
		}
	}
}
