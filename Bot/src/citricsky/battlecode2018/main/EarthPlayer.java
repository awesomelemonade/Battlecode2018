package citricsky.battlecode2018.main;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.unithandler.UnitHandler;
import citricsky.battlecode2018.unithandler.WorkerHarvestHandler;
import citricsky.battlecode2018.util.Util;

public class EarthPlayer {
	private static PlanMap planMap;

	public static void execute() {
		GameController gc = GameController.INSTANCE;
		planMap = new PlanMap(Planet.EARTH.getStartingMap());
		UnitType[] researchOrder = new UnitType[]
				{UnitType.KNIGHT, UnitType.KNIGHT, UnitType.KNIGHT, UnitType.ROCKET,
						UnitType.HEALER, UnitType.HEALER, UnitType.HEALER};
		for (UnitType research : researchOrder) {
			gc.queueResearch(research);
		}
		Map<UnitType, Set<Function<Unit, UnitHandler>>> handlers = new HashMap<UnitType, Set<Function<Unit, UnitHandler>>>();
		for(UnitType unitType: UnitType.values()) {
			handlers.put(unitType, new HashSet<Function<Unit, UnitHandler>>());
		}
		handlers.get(UnitType.WORKER).add(unit -> new WorkerHarvestHandler(unit));
		while (true) {
			Unit[] myUnits = gc.getMyUnits();
			Map<UnitHandler, Integer> priorities = new HashMap<UnitHandler, Integer>();
			PriorityQueue<UnitHandler> queue = new PriorityQueue<UnitHandler>(myUnits.length, new Comparator<UnitHandler>() {
				@Override
				public int compare(UnitHandler a, UnitHandler b) {
					return Integer.compare(priorities.get(a), priorities.get(b));
				}
			});
			for(Unit unit: myUnits) {
				UnitHandler bestHandler = null;
				int bestPriority = -Integer.MAX_VALUE;
				for(Function<Unit, UnitHandler> function: handlers.get(unit.getType())) {
					UnitHandler handler = function.apply(unit);
					int priority = handler.getPriority(bestPriority);
					if(priority > bestPriority) {
						bestPriority = priority;
						bestHandler = handler;
					}
				}
				if(bestHandler != null) {
					priorities.put(bestHandler, bestPriority);
					queue.add(bestHandler);
				}
			}
			while(!queue.isEmpty()) {
				UnitHandler handler = queue.poll();
				handler.execute();
			}
			
			//Unit[] enemyRobots = gc.getAllUnitsByFilter((unit -> unit.getTeam() != gc.getTeam() && !unit.isStructure()));

			boolean enemyInRange = true;
			for (Unit unit : gc.getMyUnitsByFilter(unit -> !unit.isStructure() && unit.getLocation().isOnMap())) {
				if (unit.senseNearbyUnitsByTeam((long) unit.getVisionRange(), gc.getEnemyTeam()).length > 0) {
					enemyInRange = false;
					break;
				}
			}

			for (Unit unit : gc.getMyUnitsByFilter(unit -> unit.getLocation().isOnMap())) { // Using a predicate up here, so there is a smaller array in memory
				try {
					if (!enemyInRange) {
						//TODO: do something
					}
					if (unit.getType() == UnitType.WORKER) {
						if (!tryBlueprint(unit, UnitType.FACTORY)) {
							if (!tryBuild(unit)) {
								BFSDestination bfs = new BFSDestination(planMap.getWidth(), planMap.getHeight(),
										unit.getLocation().getMapLocation().getPosition());
								bfs.process(vector -> planMap.isPassable(vector), vector -> {
									MapLocation location = Planet.EARTH.getMapLocation(vector);
									if(gc.canSenseLocation(location)){
										if(location.getKarboniteCount()>0) {
											return true;
										}
										if(location.hasUnitAtLocation()) {
											if(location.getUnit().isStructure()) {
												return !location.getUnit().isStructureBuilt();
											}
										}
									}else {
										if(planMap.getKarbonite(vector)>0) {
											return true;
										}
									}
									return false;
								});
								if(bfs.getQueue().isEmpty()) {
									Direction direction = Direction.randomDirection();
									if (unit.isMoveReady() && unit.canMove(direction)) {
										unit.move(direction);
									}
								}else {
									Vector workLocation = bfs.getQueue().peekLast();
									Direction direction = bfs.trace(workLocation, unit.getLocation().getMapLocation().getPosition());
									if(unit.isMoveReady() && unit.canMove(direction)) {
										unit.move(direction);
									}
								}
							}
						}
						//TODO: Harvesting of Karbonite
						continue;
					}
					if (unit.getType() == UnitType.FACTORY) {
						if (unit.canProduceRobot(UnitType.KNIGHT)) {
							unit.produceRobot(UnitType.KNIGHT);
						}
						int garrisonSize = unit.getGarrisonUnitIds().length;
						if (garrisonSize > 0) {
							for (Direction direction : Direction.values()) {
								if (unit.canUnload(direction)) {
									unit.unload(direction);
									if (--garrisonSize == 0) {
										break;
									}
								}
							}
						}
					}
					if (unit.getType() == UnitType.KNIGHT) {
						BFSDestination bfs = new BFSDestination(planMap.getWidth(), planMap.getHeight(),
								unit.getLocation().getMapLocation().getPosition());
						bfs.process(vector -> planMap.isPassable(vector),
								vector -> {
									for(Direction direction: Direction.CARDINAL_DIRECTIONS) {
										MapLocation location = Planet.EARTH.getMapLocation(vector.add(direction.getOffsetVector()));
										if(gc.canSenseLocation(location)) {
											if(location.hasUnitAtLocation()) {
												if(location.getUnit().getTeam() == gc.getEnemyTeam()) {
													return true;
												}
											}
										}
									}
									return false;
								});
						if (bfs.getQueue().isEmpty()) {
							Direction direction = Direction.randomDirection();
							if (unit.isMoveReady() && unit.canMove(direction)) {
								unit.move(direction);
							}
						} else {
							Vector movePosition = bfs.getQueue().peekLast();
							Direction direction = bfs.trace(movePosition, unit.getLocation().getMapLocation().getPosition()).getOpposite();
							if (unit.isMoveReady() && unit.canMove(direction)) {
								unit.move(direction);
							}
							for(Direction attackDirection: Direction.CARDINAL_DIRECTIONS) {
								MapLocation candidate = Planet.EARTH.getMapLocation(movePosition.add(attackDirection.getOffsetVector()));
								if(candidate.hasUnitAtLocation()) {
									Unit enemyUnit = candidate.getUnit();
									if(enemyUnit.getTeam() == gc.getEnemyTeam()) {
										if(unit.isJavelinReady() && unit.canJavelin(enemyUnit)){
											unit.javelin(enemyUnit);
										}
										if(unit.isAttackReady() && unit.canAttack(enemyUnit)) {
											unit.attack(enemyUnit);
										}
										break;
									}
								}
							}
						}
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
			gc.yield();
		}
	}

	public static boolean tryRepair(Unit unit) {
		Unit bestTarget = null;
		int bestHealth = 0;
		for (Direction direction : Direction.values()) {
			MapLocation offsetLocation = unit.getLocation().getMapLocation().getOffsetLocation(direction);
			if (offsetLocation.hasUnitAtLocation()) {
				Unit buildTarget = offsetLocation.getUnit();
				if (buildTarget.getType() == UnitType.FACTORY || buildTarget.getType() == UnitType.ROCKET) {
					if (bestHealth < buildTarget.getHealth() && unit.canRepair(buildTarget)) {
						bestTarget = buildTarget;
						bestHealth = buildTarget.getHealth();
					}
				}
			}
		}
		if (bestTarget != null) {
			unit.repair(bestTarget);
		}
		return bestTarget != null;
	}

	public static boolean tryBuild(Unit unit) {
		Unit bestTarget = null;
		int bestHealth = 0;
		for (Direction direction : Direction.values()) {
			MapLocation offsetLocation = unit.getLocation().getMapLocation().getOffsetLocation(direction);
			if (offsetLocation.hasUnitAtLocation()) {
				Unit buildTarget = offsetLocation.getUnit();
				if (buildTarget.getType() == UnitType.FACTORY || buildTarget.getType() == UnitType.ROCKET) {
					if (bestHealth < buildTarget.getHealth() && unit.canBuild(buildTarget)) {
						bestTarget = buildTarget;
						bestHealth = buildTarget.getHealth();
					}
				}
			}
		}
		if (bestTarget != null) {
			unit.build(bestTarget);
		}
		return bestTarget != null;
	}

	public static boolean tryBlueprint(Unit unit, UnitType type) {
		for (Direction direction : Direction.values()) {
			if (unit.canBlueprint(type, direction)) {
				MapLocation offsetLocation = unit.getLocation().getMapLocation().getOffsetLocation(direction);
				if (offsetLocation.isOnMap()) {
					int neighbors = Util.getNeighbors(offsetLocation,
							(mapLocation) -> planMap.isPassable(mapLocation.getPosition()));
					if (Util.canBuild(neighbors)) {
						unit.blueprint(type, direction);
						planMap.setStructure(offsetLocation.getPosition(), type);
						return true;
					}
				}
			}
		}
		return false;
	}
}
