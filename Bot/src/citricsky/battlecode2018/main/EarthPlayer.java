package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.unitexecutor.FactoryExecutor;
import citricsky.battlecode2018.unitexecutor.RocketExecutor;
import citricsky.battlecode2018.unitexecutor.UnitExecutor;
import citricsky.battlecode2018.util.Benchmark;

public class EarthPlayer {
	public static void execute() {
		Benchmark benchmark = new Benchmark();
		GameController gc = GameController.INSTANCE;
		UnitType[] researchOrder = new UnitType[]
				{UnitType.WORKER, //finishes at Round 25
				 UnitType.KNIGHT, //finishes at Round 50
				 UnitType.KNIGHT, //finishes at Round 125
				 UnitType.KNIGHT, //finishes at Round 225
				 UnitType.RANGER, //finishes at Round 250
				 UnitType.ROCKET, //finishes at Round 350
				 UnitType.HEALER, //finishes at Round 375
				 UnitType.HEALER, //finishes at Round 475
				 UnitType.WORKER, //finishes at Round 550
				 UnitType.HEALER, //finishes at Round 650
				 UnitType.MAGE, //finishes at Round 675
				 UnitType.MAGE, //finishes at Round 750
				 UnitType.MAGE, //finishes at Round 850
				 }; 
		for (UnitType research : researchOrder) {
			gc.queueResearch(research);
		}
		UnitExecutor[] executors = new UnitExecutor[UnitType.values().length];
		MoveManager moveManager = new MoveManager();
		
		executors[UnitType.FACTORY.ordinal()] = new FactoryExecutor(moveManager);
		executors[UnitType.ROCKET.ordinal()] = new RocketExecutor(moveManager);
		
		while (true) {
			benchmark.push();
			if (benchmark.peek() / 1000000 < gc.getTimeLeft() - 2000) {
				RoundInfo.update();
				for (UnitExecutor executor: executors) {
					benchmark.push();
					executor.update();
					double deltaTime = benchmark.pop() / 1000000.0;
					if (deltaTime > 10) {
						System.out.println("Update: " + executor.getClass().getSimpleName() + " - " + deltaTime + "ms");
					}
				}
				moveManager.update();
				for (Unit unit: RoundInfo.getMyUnits()) {
					executors[unit.getType().ordinal()].execute(unit);
				}
			} else {
				System.out.println("Skipping Round: " + gc.getRoundNumber() + " - " + gc.getTimeLeft() + "ms");
			}
			double deltaTime = benchmark.pop() / 1000000.0;
			if(deltaTime > 20) {
				System.out.println("Round Time: " + gc.getRoundNumber() + " - " + deltaTime + "/" + gc.getTimeLeft() + "ms");
			}
			gc.yield();
		}
	}
}
