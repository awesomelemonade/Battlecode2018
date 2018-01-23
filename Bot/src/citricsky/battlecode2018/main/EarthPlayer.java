package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.unitexecutor.FactoryExecutor;
import citricsky.battlecode2018.unitexecutor.HealerExecutor;
import citricsky.battlecode2018.unitexecutor.KnightExecutor;
import citricsky.battlecode2018.unitexecutor.RangerExecutor;
import citricsky.battlecode2018.unitexecutor.RocketExecutor;
import citricsky.battlecode2018.unitexecutor.UnitExecutor;
import citricsky.battlecode2018.unitexecutor.WorkerExecutor;
import citricsky.battlecode2018.util.Benchmark;

public class EarthPlayer {
	public static void execute() {
		Benchmark benchmark = new Benchmark();
		GameController gc = GameController.INSTANCE;
		/*UnitType[] researchOrder = new UnitType[]
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
				 }; */
		UnitType[] researchOrder = new UnitType[] {
				UnitType.WORKER, UnitType.ROCKET, UnitType.RANGER, UnitType.HEALER,
				UnitType.HEALER, UnitType.HEALER, UnitType.RANGER, UnitType.RANGER
		};
		for (UnitType research : researchOrder) {
			gc.queueResearch(research);
		}
		UnitExecutor[] executors = new UnitExecutor[UnitType.values().length];
		MoveManager moveManager = new MoveManager();
		PlanetCommunication communication = new PlanetCommunication();
		
		executors[UnitType.FACTORY.ordinal()] = new FactoryExecutor(moveManager);
		executors[UnitType.ROCKET.ordinal()] = new RocketExecutor(moveManager, communication);
		executors[UnitType.RANGER.ordinal()] = new RangerExecutor();
		executors[UnitType.KNIGHT.ordinal()] = new KnightExecutor();
		executors[UnitType.HEALER.ordinal()] = new HealerExecutor();
		executors[UnitType.MAGE.ordinal()] = null;
		executors[UnitType.WORKER.ordinal()] = new WorkerExecutor(moveManager);
		
		while (true) {
			benchmark.push();
			if (benchmark.peek() / 1000000 < gc.getTimeLeft() - 2000) {
				RoundInfo.update();
				communication.update();
				if (benchmark.peek() / 1000000 < gc.getTimeLeft() - 3000) {
					moveManager.updateBFS();
				} else {
					System.out.println("Skipping BFS Update");
				}
				moveManager.move(unit -> {
					if(executors[unit.getType().ordinal()] != null) {
						executors[unit.getType().ordinal()].execute(unit);
					}
				});
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
