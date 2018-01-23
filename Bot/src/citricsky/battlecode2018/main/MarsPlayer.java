package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.unitexecutor.HealerExecutor;
import citricsky.battlecode2018.unitexecutor.KnightExecutor;
import citricsky.battlecode2018.unitexecutor.RangerExecutor;
import citricsky.battlecode2018.unitexecutor.RocketExecutor;
import citricsky.battlecode2018.unitexecutor.UnitExecutor;
import citricsky.battlecode2018.unitexecutor.WorkerExecutor;
import citricsky.battlecode2018.util.Benchmark;

public class MarsPlayer {
	public static void execute() {
		Benchmark benchmark = new Benchmark();
		GameController gc = GameController.INSTANCE;
		UnitExecutor[] executors = new UnitExecutor[UnitType.values().length];
		MoveManager moveManager = new MoveManager();
		
		executors[UnitType.FACTORY.ordinal()] = null;
		executors[UnitType.ROCKET.ordinal()] = new RocketExecutor(moveManager);
		executors[UnitType.RANGER.ordinal()] = new RangerExecutor();
		executors[UnitType.KNIGHT.ordinal()] = new KnightExecutor();
		executors[UnitType.HEALER.ordinal()] = new HealerExecutor();
		executors[UnitType.MAGE.ordinal()] = null;
		executors[UnitType.WORKER.ordinal()] = new WorkerExecutor(moveManager);
		
		while (true) {
			benchmark.push();
			if (benchmark.peek() / 1000000 < gc.getTimeLeft() - 2000) {
				RoundInfo.update();
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
