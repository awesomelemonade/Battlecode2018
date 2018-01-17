package citricsky.battlecode2018.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.task.*;
import citricsky.battlecode2018.unithandler.*;
import citricsky.battlecode2018.util.Util;

public class EarthPlayer {
	public static void execute() {
		GameController gc = GameController.INSTANCE;
		UnitType[] researchOrder = new UnitType[]
				{UnitType.RANGER, UnitType.RANGER, UnitType.ROCKET, UnitType.KNIGHT, UnitType.KNIGHT,
						UnitType.KNIGHT, UnitType.ROCKET, UnitType.ROCKET, UnitType.WORKER, UnitType.WORKER,
						UnitType.WORKER, UnitType.WORKER, UnitType.HEALER, UnitType.HEALER, UnitType.HEALER};
		for (UnitType research : researchOrder) {
			gc.queueResearch(research);
		}
		Map<UnitType, Set<PathfinderTask>> pathfinderTasks = new HashMap<UnitType, Set<PathfinderTask>>();
		Map<UnitType, Set<Function<Unit, UnitHandler>>> handlers = new HashMap<UnitType, Set<Function<Unit, UnitHandler>>>();
		for (UnitType unitType : UnitType.values()) {
			handlers.put(unitType, new HashSet<Function<Unit, UnitHandler>>());
			pathfinderTasks.put(unitType, new HashSet<PathfinderTask>());
		}
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerBlueprintFactoryTask());
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerBlueprintRocketTask());
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerHarvestTask());
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerBuildTask());
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerReplicateTask());
		pathfinderTasks.get(UnitType.KNIGHT).add(new KnightAttackTask());
		pathfinderTasks.get(UnitType.RANGER).add(new RangerAttackTask());
		pathfinderTasks.get(UnitType.MAGE).add(new MageAttackTask());
		pathfinderTasks.get(UnitType.HEALER).add(new HealerHealTask());

		handlers.get(UnitType.FACTORY).add(FactoryHandler::new);
		handlers.get(UnitType.ROCKET).add(RocketHandler::new);
		handlers.get(UnitType.KNIGHT).add(ExploreHandler::new);
		handlers.get(UnitType.RANGER).add(ExploreHandler::new);
		handlers.get(UnitType.MAGE).add(ExploreHandler::new);
		handlers.get(UnitType.HEALER).add(ExploreHandler::new);
		handlers.get(UnitType.WORKER).add(ExploreHandler::new);

		Set<MapLocation> occupied = new HashSet<MapLocation>();
		for (UnitType unitType : UnitType.values()) {
			if (!pathfinderTasks.get(unitType).isEmpty()) {
				handlers.get(unitType).add(unit -> new BFSHandler(unit, location -> {
					for(Unit enemy: RoundInfo.getEnemiesOnMap()) {
						if(enemy.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition()) <=
								enemy.getType().getBaseVisionRange()) {
							return false;
						}
					}
					return Util.PASSABLE_PREDICATE.test(location);
				}, occupied,
						pathfinderTasks.get(unitType).toArray(new PathfinderTask[pathfinderTasks.get(unitType).size()])));
			}
		}
		int lastRoundNumber = GameController.INSTANCE.getRoundNumber();
		while (true) {
			RoundInfo.update();
			if(RoundInfo.getRoundNumber() > lastRoundNumber + 1) {
				System.out.println("Skipped Round? "+lastRoundNumber+" - "+RoundInfo.getRoundNumber());
			}
			lastRoundNumber = RoundInfo.getRoundNumber();
			//System.out.println("Round: " + GameController.INSTANCE.getRoundNumber() + " Time: " + GameController.INSTANCE.getTimeLeft() + "ms Karbonite: " + GameController.INSTANCE.getCurrentKarbonite());
			occupied.clear();
			for(UnitType unitType : UnitType.values()) {
				pathfinderTasks.get(unitType).forEach(PathfinderTask::update);
			}
			Unit[] myUnits = gc.getMyUnits();
			Map<Unit, Set<UnitHandler>> map = new HashMap<Unit, Set<UnitHandler>>();
			for (Unit unit : myUnits) {
				map.put(unit, new HashSet<UnitHandler>());
				for (Function<Unit, UnitHandler> function : handlers.get(unit.getType())) {
					map.get(unit).add(function.apply(unit));
				}
			}
			while (gc.getTimeLeft() > 1000) {
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
			gc.yield();
		}
	}
}
