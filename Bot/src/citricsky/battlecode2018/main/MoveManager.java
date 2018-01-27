package citricsky.battlecode2018.main;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.Consumer;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.unitexecutor.WorkerExecutor;
import citricsky.battlecode2018.util.Benchmark;
import citricsky.battlecode2018.util.Constants;
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
	public static final int BFS_FIND_COMBAT_ENEMY = 0;
	public static final int BFS_FIND_ALL_ENEMY = 1;
	public static final int BFS_FIND_HEAL = 2;
	public static final int BFS_WORKER_TASK = 3;
	public static final int BFS_WORKER_BLUEPRINT = 4;
	public static final int BFS_WORKER_HARVEST = 5;
	public static final int BFS_KNIGHT_ATTACK = 6;
	public static final int BFS_RANGER_ATTACK = 7;
	public static final int BFS_HEALER_HEAL = 8;
	public static final int BFS_HEALER_IDLE = 9;
	public static final int BFS_LOAD_ROCKET = 10;
	public static final int BFS_EXPLORE = 11;
	private BFS[] bfsArray;
	private boolean[] processed;
	private Planet planet;
	private int[][] karbonite;
	private boolean[][] explored;
	private int[][] blueprint;
	private int[] priorities;
	private int[] bfsIndices;
	private PriorityQueue<Unit> queue;
	
	public MoveManager() {
		priorities = new int[Constants.MAX_UNIT_ID];
		bfsIndices = new int[Constants.MAX_UNIT_ID];
		queue = new PriorityQueue<Unit>(7, new Comparator<Unit>() {
			@Override
			public int compare(Unit a, Unit b) {
				return Integer.compare(priorities[a.getId()], priorities[b.getId()]);
			}
		});
		this.planet = GameController.INSTANCE.getPlanet();
		this.karbonite = new int[planet.getWidth()][planet.getHeight()];
		this.explored = new boolean[planet.getWidth()][planet.getHeight()];
		this.blueprint = new int[planet.getWidth()][planet.getHeight()];
		//Initialize bfsArray
		this.bfsArray = new BFS[12];
		this.processed = new boolean[bfsArray.length];
		for (int i = 0; i < bfsArray.length; ++i) {
			bfsArray[i] = new BFS(planet.getWidth(), planet.getHeight(),
					vector -> Util.PASSABLE_PREDICATE.test(planet.getMapLocation(vector)));
		}
		if (planet == Planet.EARTH) {
			for (int i = 0; i < planet.getWidth(); ++i) {
				for (int j = 0; j < planet.getHeight(); ++j) {
					karbonite[i][j] = planet.getStartingMap().getInitialKarboniteAt(planet.getMapLocation(i, j));
				}
			}
		}
	}
	public int getBlueprint(int x, int y) {
		return blueprint[x][y];
	}
	public boolean isNearEnemy(Vector position, int threshold) {
		for (Unit unit: RoundInfo.getEnemiesOnMap()) {
			int distanceSquared = unit.getLocation().getMapLocation().getPosition().getDistanceSquared(position);
			if (distanceSquared < threshold) {
				return true;
			}
		}
		return false;
	}
	public void updateBFS() {
		for (int i = 0; i < bfsArray.length; ++i) {
			bfsArray[i].resetHard();
			processed[i] = false;
		}
		if (RoundInfo.getEnemiesOnMap().length > 0) {
			for (Unit unit: RoundInfo.getEnemiesOnMap()) {
				MapLocation location = unit.getLocation().getMapLocation();
				if (unit.getType().isCombatType()) {
					bfsArray[BFS_FIND_COMBAT_ENEMY].addSource(location.getPosition());
				}
				bfsArray[BFS_FIND_ALL_ENEMY].addSource(location.getPosition());
				addSource(BFS_KNIGHT_ATTACK, location, Direction.COMPASS);
				for (int i = 0; i < RANGER_OFFSET_X.length; ++i) {
					Vector offset = location.getPosition().add(RANGER_OFFSET_X[i], RANGER_OFFSET_Y[i]);
					if (!Util.outOfBounds(offset, planet.getWidth(), planet.getHeight())) {
						if (Util.PASSABLE_PREDICATE.test(planet.getMapLocation(offset))) {
							if (!isNearEnemy(offset, 40)) {
								bfsArray[BFS_RANGER_ATTACK].addSource(offset);
							}
						}
					}
				}
			}
		} else {
			for (Unit unit: planet.getStartingMap().getInitialUnits()) {
				if (unit.getTeam() == GameController.INSTANCE.getEnemyTeam()) {
					Vector position = unit.getLocation().getMapLocation().getPosition();
					if (!explored[position.getX()][position.getY()]) {
						bfsArray[BFS_FIND_ALL_ENEMY].addSource(position);
					}
				}
			}
		}
		for (Unit unit: RoundInfo.getMyUnits()) {
			if (unit.getLocation().isOnMap()) {
				MapLocation location = unit.getLocation().getMapLocation();
				boolean nearEnemy = nearEnemy(location.getPosition(), 12, false);
				if (unit.getHealth() < unit.getMaxHealth()) {
					if (!nearEnemy && unit.getType().isStructure()) {
						addSource(BFS_WORKER_TASK, location, Direction.COMPASS);
					}
				}
				boolean damagedHealerTarget = false;
				boolean idleHealerTarget = false;
				if (!unit.getType().isStructure()) {
					if (unit.getHealth() < unit.getMaxHealth()) {
						damagedHealerTarget = true;
					} else {
						idleHealerTarget = true;
					}
				}
				if (damagedHealerTarget || idleHealerTarget) {
					for (int i = 0; i < HEALER_OFFSET_X.length; ++i) {
						Vector offset = location.getPosition().add(HEALER_OFFSET_X[i], HEALER_OFFSET_Y[i]);
						if (!Util.outOfBounds(offset, planet.getWidth(), planet.getHeight())) {
							if (Util.PASSABLE_PREDICATE.test(planet.getMapLocation(offset)) &&
									getBFSStep(BFS_FIND_COMBAT_ENEMY, offset) >= 12) {
								if (damagedHealerTarget) {
									bfsArray[BFS_HEALER_HEAL].addSource(offset);
								}
								if (idleHealerTarget) {
									bfsArray[BFS_HEALER_IDLE].addSource(offset);
								}
							}
						}
					}
				}
				if (unit.getType() == UnitType.ROCKET && unit.isStructureBuilt() &&
						unit.getGarrisonUnitIds().length < unit.getStructureMaxCapacity()) {
					bfsArray[BFS_LOAD_ROCKET].addSource(location.getPosition());
				}
				if (unit.getType() == UnitType.HEALER) {
					bfsArray[BFS_FIND_HEAL].addSource(location.getPosition()); //simplicity sake, you could probably precompute offsets later
				}
			}
		}
		for (int i = 0; i < planet.getWidth(); ++i) {
			for (int j = 0; j < planet.getHeight(); ++j) {
				MapLocation location = planet.getMapLocation(i, j);
				boolean nearEnemy = nearEnemy(location.getPosition(), 12, false);
				if (GameController.INSTANCE.canSenseLocation(location)) {
					explored[i][j] = !nearEnemy;
					karbonite[i][j] = location.getKarboniteCount();
				} else {
					if (planet == Planet.MARS && (!nearEnemy)) {
						bfsArray[BFS_EXPLORE].addSource(location.getPosition());
					}
				}
				if (planet == Planet.EARTH) {
					if (!explored[i][j] && (!nearEnemy)) {
						bfsArray[BFS_EXPLORE].addSource(location.getPosition());
					}
				}
				if (!nearEnemy) {
					if (karbonite[i][j] > 0) {
						bfsArray[BFS_WORKER_HARVEST].addSource(location.getPosition());
					} else {
						if (Util.PASSABLE_PREDICATE.test(location) && !isNextToStructure(location)) {
							int neighbors = Util.getNeighbors(location, Util.PASSABLE_PREDICATE.negate());
							int buildArray = Util.getBuildArray(neighbors);
							blueprint[i][j] = buildArray;
						} else {
							blueprint[i][j] = -1;
						}
					}
				}
			}
		}
		for (int i = 0; i < planet.getWidth(); ++i) {
			for (int j = 0; j < planet.getHeight(); ++j) {
				MapLocation location = planet.getMapLocation(i, j);
				if (Util.PASSABLE_PREDICATE.test(location)) {
					for (Direction direction: Direction.COMPASS) {
						Vector offset = location.getOffsetLocation(direction).getPosition();
						if (!Util.outOfBounds(offset, blueprint.length, blueprint[0].length)) {
							if (blueprint[offset.getX()][offset.getY()] > 0) {
								bfsArray[BFS_WORKER_BLUEPRINT].addSource(location.getPosition());
								break;
							}
						}
					}
				}
			}
		}
	}
	public boolean isNextToStructure(MapLocation location) {
		for (Direction dir: Direction.COMPASS) {
			Vector offset = location.getPosition().add(dir.getOffsetVector());
			if (RoundInfo.hasStructure(offset.getX(), offset.getY())) {
				return true;
			}
		}
		return false;
	}
	public boolean nearEnemy(Vector position, int moveDistance, boolean all) {
		for (Direction direction: Direction.COMPASS) {
			if (getBFSStep(all?BFS_FIND_ALL_ENEMY:BFS_FIND_COMBAT_ENEMY, position.add(direction.getOffsetVector())) < moveDistance) {
				return true;
			}
		}
		return false;
	}
	public void addSource(int index, MapLocation location, Direction[] directions) {
		for (Direction direction: directions) {
			MapLocation offset = location.getOffsetLocation(direction);
			if (Util.PASSABLE_PREDICATE.test(offset)) {
				bfsArray[index].addSource(offset.getPosition());
			}
		}
	}
	public void queueUnit(Unit unit) {
		Vector position = unit.getLocation().getMapLocation().getPosition();
		if (unit.getType().isStructure()) {
			if (unit.getType() == UnitType.FACTORY) {
				priorities[unit.getId()] = -bfsArray[BFS_FIND_COMBAT_ENEMY].getStep(position.getX(), position.getY());
			} else {
				priorities[unit.getId()] = Integer.MIN_VALUE;
			}
		} else {
			int bfsIndex = getBFSIndex(unit);
			bfsIndices[unit.getId()] = bfsIndex;
			if (bfsIndex == BFS_EXPLORE) {
				priorities[unit.getId()] = Integer.MIN_VALUE;
			} else {
				int score = -bfsArray[bfsIndex].getStep(position.getX(), position.getY());
				if (bfsIndex == BFS_WORKER_TASK && score == BFS.SOURCE_STEP) {
					score = Integer.MAX_VALUE; // Workers building or repairing get priority in replication
				}
				priorities[unit.getId()] = score;
			}
		}
		queue.add(unit);
	}
	public void move(Consumer<Unit> executor) {
		Unit[] units = GameController.INSTANCE.getMyUnitsByFilter(
				unit -> unit.getLocation().isOnMap());
		if (units.length > 0) {
			queue.clear();
			for (Unit unit: units) {
				queueUnit(unit);
			}
			while (!queue.isEmpty()) {
				try {
					Unit unit = queue.poll();
					if ((!unit.getType().isStructure()) && unit.isMoveReady()) {
						if (unit.getType() == UnitType.RANGER && unit.isRangerSniping()) {
							continue;
						}
						MapLocation location = unit.getLocation().getMapLocation();
						int bfsIndex = bfsIndices[unit.getId()];
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
							} else {
								// look around for one with the same step
								for (Direction dir: Direction.COMPASS) {
									if (dir == direction) {
										continue;
									}
									int candidateStep = getBFSStep(bfsIndex, location.getPosition().add(dir.getOffsetVector()));
									if (candidateStep == step) {
										if (unit.canMove(dir)) {
											unit.move(dir);
											break;
										}
									}
								}
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
	public int getBFSIndex(Unit unit) {
		return getBFSIndex(unit.getType(), unit.getLocation().getMapLocation().getPlanet(),
				unit.getLocation().getMapLocation().getPosition(), ((double)unit.getHealth()) / ((double)unit.getMaxHealth()));
	}
	public int getBFSIndex(UnitType type, Planet planet, Vector position, double percentHealth) {
		if (percentHealth <= 0.5 && type != UnitType.HEALER && type!= UnitType.WORKER) {
			if (getBFSStep(BFS_FIND_HEAL, position) != Integer.MAX_VALUE) {
				return BFS_FIND_HEAL;
			}
		}
		if (type == UnitType.WORKER) {
			if (WorkerExecutor.getBlueprintType() != null) {
				return BFS_WORKER_BLUEPRINT;
			}
			int workerTaskStep = getBFSStep(BFS_WORKER_TASK, position);
			int workerHarvestStep = getBFSStep(BFS_WORKER_HARVEST, position);
			if (workerTaskStep == Integer.MAX_VALUE && workerHarvestStep == Integer.MAX_VALUE) {
				return BFS_WORKER_BLUEPRINT;
			}
			if (workerHarvestStep == Integer.MAX_VALUE) {
				return BFS_WORKER_TASK;
			}
			if (workerTaskStep == Integer.MAX_VALUE) {
				if (workerHarvestStep > 20) {
					return BFS_WORKER_BLUEPRINT;
				} else {
					return BFS_WORKER_HARVEST;
				}
			}
			if (workerTaskStep <= 3) {
				return BFS_WORKER_TASK;
			}
			if (workerTaskStep - 3 <= workerHarvestStep) {
				return BFS_WORKER_TASK;
			} else {
				if (workerHarvestStep > 20) {
					return BFS_WORKER_BLUEPRINT;
				} else {
					return BFS_WORKER_HARVEST;
				}
			}
		}
		if (planet == Planet.EARTH) {
			if (RoundInfo.getRoundNumber() + GameController.INSTANCE.getCurrentFlightDuration() > 800) {
				return BFS_LOAD_ROCKET;
			}
		}
		if (type == UnitType.HEALER) {
			int healerHealStep = getBFSStep(BFS_HEALER_HEAL, position);
			int healerIdleStep = getBFSStep(BFS_HEALER_IDLE, position);
			if (!(healerHealStep == Integer.MAX_VALUE && healerIdleStep == Integer.MAX_VALUE)) {
				if (healerHealStep - 5 <= healerIdleStep) {
					return BFS_HEALER_HEAL;
				} else {
					return BFS_HEALER_IDLE;
				}
			}
		}
		int bfsAttackIndex = -1;
		if (type == UnitType.KNIGHT) {
			bfsAttackIndex = BFS_KNIGHT_ATTACK;
		}
		if (type == UnitType.RANGER) {
			bfsAttackIndex = BFS_RANGER_ATTACK;
		}
		if (bfsAttackIndex == -1) {
			bfsAttackIndex = BFS_FIND_ALL_ENEMY;
		}
		int attackStep = getBFSStep(bfsAttackIndex, position);
		if (attackStep < 10) {
			return bfsAttackIndex;
		}else {
			if (planet == Planet.EARTH) {
				int bfsStep = getBFSStep(BFS_LOAD_ROCKET, position);
				if (bfsStep != Integer.MAX_VALUE && (RoundInfo.getRoundNumber() > 600 ||
						RoundInfo.getRoundNumber() > 400 && bfsStep < 12)){
					return BFS_LOAD_ROCKET;
				}
			}
			if (bfsAttackIndex != BFS_FIND_ALL_ENEMY) {
				int findStep = getBFSStep(BFS_FIND_ALL_ENEMY, position);
				if (findStep != Integer.MAX_VALUE) {
					return BFS_FIND_ALL_ENEMY;
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
		Benchmark benchmark = new Benchmark();
		benchmark.push();
		double deltaTime;
		while (bfsArray[index].getQueueSize() > 0) {
			benchmark.push();
			bfsArray[index].step();
			deltaTime = benchmark.pop() / 1000000.0;
			if (deltaTime > 20) {
				System.out.println("\t\tBFS Step: " + index + " - " + bfsArray[index].getCurrentStep() + " - " + deltaTime + "ms");
			}
		}
		deltaTime = benchmark.pop() / 1000000.0;
		if (deltaTime > 20) {
			System.out.println("\t\tProcess BFS: " + index + " - " + bfsArray[index].getCurrentStep() + " - " + deltaTime + "ms");
		}
	}
}
