package citricsky.battlecode2018.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.task.KnightAttackTask;
import citricsky.battlecode2018.task.RangerAttackTask;
import citricsky.battlecode2018.task.WorkerBlueprintFactoryTask;
import citricsky.battlecode2018.task.WorkerBuildTask;
import citricsky.battlecode2018.task.WorkerHarvestTask;
import citricsky.battlecode2018.unithandler.BFSHandler;
import citricsky.battlecode2018.unithandler.ExploreHandler;
import citricsky.battlecode2018.unithandler.FactoryHandler;
import citricsky.battlecode2018.unithandler.PathfinderTask;
import citricsky.battlecode2018.unithandler.UnitHandler;

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
		handlers.get(UnitType.FACTORY).add(FactoryHandler::new);
		handlers.get(UnitType.KNIGHT).add(ExploreHandler::new);
		handlers.get(UnitType.RANGER).add(ExploreHandler::new);
		for(UnitType unitType: pathfinderTasks.keySet()) {
			if(!pathfinderTasks.get(unitType).isEmpty()) {
				handlers.get(unitType).add(unit -> new BFSHandler(unit,
						pathfinderTasks.get(unitType).toArray(new PathfinderTask[pathfinderTasks.get(unitType).size()])));
			}
		}
		while (true) {
			System.out.println("Round: " + GameController.INSTANCE.getRoundNumber() + " Time: " + GameController.INSTANCE.getTimeLeft() + "ms");
			Set<Unit> unhandled = new HashSet<Unit>();
			for (Unit unit : gc.getMyUnits()) {
				unhandled.add(unit);
			}
			while (unhandled.size() > 0 && gc.getTimeLeft() > 1000) {
				Unit bestUnit = null;
				UnitHandler bestHandler = null;
				int bestPriority = Integer.MIN_VALUE;
				for (Unit unit : unhandled) {
					for (Function<Unit, UnitHandler> function : handlers.get(unit.getType())) {
						UnitHandler handler = function.apply(unit);
						int priority = handler.getPriority(bestPriority);
						if (priority > bestPriority) {
							bestPriority = priority;
							bestHandler = handler;
							bestUnit = unit;
						}
					}
				}
				if(bestHandler==null) {
					break;
				}
				bestHandler.execute();
				if(!bestHandler.isRequired()) {
					unhandled.remove(bestUnit);
				}
			}
			gc.yield();
		}
	}
}
