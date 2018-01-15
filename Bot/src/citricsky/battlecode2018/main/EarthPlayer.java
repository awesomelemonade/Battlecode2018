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
import citricsky.battlecode2018.task.WorkerBlueprintFactoryTask;
import citricsky.battlecode2018.task.WorkerBuildTask;
import citricsky.battlecode2018.task.WorkerHarvestTask;
import citricsky.battlecode2018.unithandler.BFSHandler;
import citricsky.battlecode2018.unithandler.FactoryHandler;
import citricsky.battlecode2018.unithandler.PathfinderTask;
import citricsky.battlecode2018.unithandler.UnitHandler;

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
		Map<UnitType, Set<PathfinderTask>> pathfinderTasks = new HashMap<UnitType, Set<PathfinderTask>>();
		Map<UnitType, Set<Function<Unit, UnitHandler>>> handlers = new HashMap<UnitType, Set<Function<Unit, UnitHandler>>>();
		for(UnitType unitType: UnitType.values()) {
			handlers.put(unitType, new HashSet<Function<Unit, UnitHandler>>());
			pathfinderTasks.put(unitType, new HashSet<PathfinderTask>());
		}
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerBlueprintFactoryTask());
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerHarvestTask());
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerBuildTask());
		handlers.get(UnitType.FACTORY).add(unit -> new FactoryHandler(unit));
		for(UnitType unitType: pathfinderTasks.keySet()) {
			if(!pathfinderTasks.get(unitType).isEmpty()) {
				handlers.get(unitType).add(unit -> new BFSHandler(unit,
						pathfinderTasks.get(unitType).toArray(new PathfinderTask[] {})));
			}
		}
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
					if(handler.isRequired()) {
						priorities.put(handler, priority);
						queue.add(handler);
					}else {
						if(priority > bestPriority) {
							bestPriority = priority;
							bestHandler = handler;
						}
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
					if (unit.getType() == UnitType.KNIGHT) {
						BFS bfs = new BFS(unit.getLocation().getMapLocation());
						bfs.process(location -> planMap.isPassable(location.getPosition()),
								location -> {
									for(Direction direction: Direction.CARDINAL_DIRECTIONS) {
										location.getOffsetLocation(direction);
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
							MapLocation moveLocation = bfs.getQueue().peekLast();
							Direction direction = bfs.getDirectionFromSource(moveLocation.getPosition());
							if (unit.isMoveReady() && unit.canMove(direction)) {
								unit.move(direction);
							}
							for(Direction attackDirection: Direction.CARDINAL_DIRECTIONS) {
								MapLocation candidate = moveLocation.getOffsetLocation(attackDirection);
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
}
