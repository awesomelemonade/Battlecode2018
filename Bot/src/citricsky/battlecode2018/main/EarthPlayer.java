package citricsky.battlecode2018.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.task.*;
import citricsky.battlecode2018.unithandler.*;

public class EarthPlayer {
	public static void execute() {
		GameController gc = GameController.INSTANCE;
		UnitType[] researchOrder = new UnitType[]
				{UnitType.KNIGHT, UnitType.KNIGHT, UnitType.KNIGHT, UnitType.ROCKET,
						UnitType.HEALER, UnitType.HEALER, UnitType.HEALER};
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
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerHarvestTask());
		pathfinderTasks.get(UnitType.WORKER).add(new WorkerBuildTask());
		pathfinderTasks.get(UnitType.KNIGHT).add(new KnightAttackTask());
		pathfinderTasks.get(UnitType.RANGER).add(new RangerAttackTask());
		pathfinderTasks.get(UnitType.MAGE).add(new MageAttackTask());

		handlers.get(UnitType.FACTORY).add(FactoryHandler::new);
		handlers.get(UnitType.KNIGHT).add(ExploreHandler::new);
		handlers.get(UnitType.RANGER).add(ExploreHandler::new);

		for (UnitType unitType : UnitType.values()) {
			handlers.get(unitType).add(unit -> new BFSHandler(unit,
					pathfinderTasks.get(unitType).toArray(new PathfinderTask[pathfinderTasks.get(unitType).size()])));
		}
		while (true) {
			System.out.println("Round: " + GameController.INSTANCE.getRoundNumber() + " Time: " + GameController.INSTANCE.getTimeLeft() + "ms");
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
						int priority = handler.getPriority(bestPriority);
						if (priority > bestPriority) {
							bestPriority = priority;
							bestHandler = handler;
							bestUnit = unit;
						}
					}
				}
				if (bestHandler == null) {
					break;
				}
				bestHandler.execute();
				if (bestHandler.isRequired()) {
					map.get(bestUnit).remove(bestHandler);
				} else {
					map.get(bestUnit).removeIf(handler -> !handler.isRequired());
				}
			}
			gc.yield();
		}
	}
}
