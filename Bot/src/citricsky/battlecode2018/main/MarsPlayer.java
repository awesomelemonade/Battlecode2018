package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.task.*;
import citricsky.battlecode2018.unithandler.*;
import citricsky.battlecode2018.util.Benchmark;
import citricsky.battlecode2018.util.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class MarsPlayer {
	public static void execute() {
		Benchmark benchmark = new Benchmark();
		GameController gc = GameController.INSTANCE;
		Map<UnitType, Set<PathfinderTask>> pathfinderTasks = new HashMap<UnitType, Set<PathfinderTask>>();
		Map<UnitType, Set<Function<Unit, UnitHandler>>> handlers = new HashMap<UnitType, Set<Function<Unit, UnitHandler>>>();
		for (UnitType unitType : UnitType.values()) {
			handlers.put(unitType, new HashSet<Function<Unit, UnitHandler>>());
			pathfinderTasks.put(unitType, new LinkedHashSet<PathfinderTask>());
		}
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerReplicateTask());
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerHarvestTask());
		pathfinderTasks.get(UnitType.KNIGHT).add(new KnightAttackTask());
		pathfinderTasks.get(UnitType.RANGER).add(new RangerAttackTask());
		pathfinderTasks.get(UnitType.MAGE).add(new MageAttackTask());
		pathfinderTasks.get(UnitType.HEALER).add(new HealerHealTask());

		handlers.get(UnitType.KNIGHT).add(ExploreHandler::new);
		handlers.get(UnitType.RANGER).add(ExploreHandler::new);
		handlers.get(UnitType.MAGE).add(ExploreHandler::new);
		handlers.get(UnitType.HEALER).add(ExploreHandler::new);
		handlers.get(UnitType.WORKER).add(ExploreHandler::new);

		Set<MapLocation> occupied = new HashSet<MapLocation>();
		for (UnitType unitType : UnitType.values()) {
			if (!pathfinderTasks.get(unitType).isEmpty()) {
				if(unitType == UnitType.WORKER) {
					handlers.get(unitType).add(unit -> new BFSHandler(unit, location -> {
						for(Unit enemy: RoundInfo.getEnemiesOnMap()) {
							if(enemy.isStructure() || enemy.getType() == UnitType.WORKER) {
								continue;
							}
							if(enemy.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition()) <=
									enemy.getType().getBaseVisionRange()) {
								return false;
							}
						}
						return Util.PASSABLE_PREDICATE.test(location);
					}, occupied,
							pathfinderTasks.get(unitType).toArray(new PathfinderTask[pathfinderTasks.get(unitType).size()])));
				} else {
					handlers.get(unitType).add(unit -> new BFSHandler(unit, Util.PASSABLE_PREDICATE, occupied,
							pathfinderTasks.get(unitType).toArray(new PathfinderTask[pathfinderTasks.get(unitType).size()])));
				}
			}
		}
		while (true) {
			benchmark.push();
			RoundInfo.update();
			//System.out.println("Round: " + GameController.INSTANCE.getRoundNumber() + " Time: " + GameController.INSTANCE.getTimeLeft() + "ms Karbonite: " + GameController.INSTANCE.getCurrentKarbonite());
			occupied.clear();
			for(UnitType unitType : UnitType.values()) {
				for (PathfinderTask task : pathfinderTasks.get(unitType)) {
					benchmark.push();
					task.update();
					double deltaTime = benchmark.pop() / 1000000.0;
					if (deltaTime > 10) {
						System.out.println("Update: " + task.getClass().getSimpleName() + " - " + deltaTime + "ms");
					}
				}
			}
			Unit[] myUnits = gc.getMyUnits();
			Map<Unit, Set<UnitHandler>> map = new HashMap<Unit, Set<UnitHandler>>();
			for (Unit unit : myUnits) {
				map.put(unit, new HashSet<UnitHandler>());
				for (Function<Unit, UnitHandler> function : handlers.get(unit.getType())) {
					map.get(unit).add(function.apply(unit));
				}
			}
			while (benchmark.peek() / 1000000 < gc.getTimeLeft() - 1000) {
				Unit bestUnit = null;
				UnitHandler bestHandler = null;
				int bestPriority = Integer.MIN_VALUE;
				for (Unit unit : myUnits) {
					for (UnitHandler handler : map.get(unit)) {
						try {
							int priority = handler.getPriority(bestPriority);
							if (priority > bestPriority) {
								bestPriority = priority;
								bestHandler = handler;
								bestUnit = unit;
							}
						} catch (Exception ex) {
							System.out.println(ex.getMessage());
							ex.printStackTrace();
						}
					}
				}
				if (bestHandler == null) {
					break;
				}
				try {
					bestHandler.execute();
					if (bestHandler.isRequired()) {
						map.get(bestUnit).remove(bestHandler);
					} else {
						map.get(bestUnit).removeIf(handler -> !handler.isRequired());
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
					ex.printStackTrace();
				}
			}
			double deltaTime = benchmark.pop() / 1000000.0;
			if(deltaTime > 20) {
				System.out.println("Round: " + RoundInfo.getRoundNumber() + " - " + deltaTime + "/" + gc.getTimeLeft() + "ms");
			}
			gc.yield();
		}
	}
}
