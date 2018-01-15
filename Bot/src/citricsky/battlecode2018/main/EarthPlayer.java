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
import citricsky.battlecode2018.unithandler.BFSHandler;
import citricsky.battlecode2018.unithandler.ExploreHandler;
import citricsky.battlecode2018.unithandler.FactoryHandler;
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
		Map<UnitType, Set<Function<Unit, UnitHandler>>> handlers = new HashMap<UnitType, Set<Function<Unit, UnitHandler>>>();
		for (UnitType unitType : UnitType.values()) {
			handlers.put(unitType, new HashSet<Function<Unit, UnitHandler>>());
		}

		handlers.get(UnitType.WORKER).add(unit -> new BFSHandler(unit,
				WorkerBlueprintFactoryTask.INSTANCE,
				WorkerHarvestTask.INSTANCE,
				WorkerBuildTask.INSTANCE
		));

		handlers.get(UnitType.KNIGHT).add(ExploreHandler::new);
		handlers.get(UnitType.KNIGHT).add(unit -> new BFSHandler(unit, KnightAttackTask.INSTANCE));

		handlers.get(UnitType.RANGER).add(ExploreHandler::new);
		handlers.get(UnitType.RANGER).add(unit -> new BFSHandler(unit, RangerAttackTask.INSTANCE));

		handlers.get(UnitType.MAGE).add(ExploreHandler::new);
		handlers.get(UnitType.MAGE).add(unit -> new BFSHandler(unit, MageAttackTask.INSTANCE));

		handlers.get(UnitType.FACTORY).add(FactoryHandler::new);

		while (true) {
			System.out.println("Round: " + GameController.INSTANCE.getRoundNumber() + " Time: " + GameController.INSTANCE.getTimeLeft() + "ms");
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
