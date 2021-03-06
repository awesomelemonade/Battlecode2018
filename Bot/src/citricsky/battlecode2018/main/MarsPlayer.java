package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.unitexecutor.FactoryExecutor;
import citricsky.battlecode2018.unitexecutor.HealerExecutor;
import citricsky.battlecode2018.unitexecutor.KnightExecutor;
import citricsky.battlecode2018.unitexecutor.MageExecutor;
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
		PlanetCommunication communication = new PlanetCommunication();
		
		executors[UnitType.FACTORY.ordinal()] = new FactoryExecutor(moveManager);
		executors[UnitType.ROCKET.ordinal()] = new RocketExecutor(moveManager, communication);
		executors[UnitType.RANGER.ordinal()] = new RangerExecutor();
		executors[UnitType.KNIGHT.ordinal()] = new KnightExecutor();
		executors[UnitType.HEALER.ordinal()] = new HealerExecutor(moveManager);
		executors[UnitType.MAGE.ordinal()] = new MageExecutor();
		executors[UnitType.WORKER.ordinal()] = new WorkerExecutor(moveManager);
		
		while (true) {
			try {
				benchmark.push();
				if (benchmark.peek() / 1000000 < gc.getTimeLeft() - 2000) {
					benchmark.push();
					RoundInfo.update();
					communication.update();
					debugPop(benchmark, 5, "Round Update Time: %fms");
					if ((benchmark.peek() / 1000000 < gc.getTimeLeft() - 3000) || (RoundInfo.getRoundNumber() % 5 == 0)) {
						try {
							benchmark.push();
							moveManager.updateBFS();
							debugPop(benchmark, 5, "UpdateBFS Time: %fms");
						} catch (Exception ex) {
							System.out.println("BFS Exception: " + ex.getMessage());
							ex.printStackTrace();
						}
					} else {
						System.out.println("Skipping BFS Update");
					}
					benchmark.push();
					moveManager.move(unit -> {
						try {
							if(executors[unit.getType().ordinal()] != null) {
								benchmark.push();
								executors[unit.getType().ordinal()].execute(unit);
								debugPop(benchmark, 5, "Execution Time: " + unit.getType() + " - %fms");
							}
						} catch (Exception ex) {
							System.out.println("Execution Exception: "+ ex.getMessage());
							ex.printStackTrace();
						}
					}, unit -> {
						try {
							if(executors[unit.getType().ordinal()] != null) {
								benchmark.push();
								executors[unit.getType().ordinal()].postExecute(unit);
								debugPop(benchmark, 5, "Post Execution Time: " + unit.getType() + " - %fms");
							}
						} catch (Exception ex) {
							System.out.println("Execution Exception: "+ ex.getMessage());
							ex.printStackTrace();
						}
					});
				} else {
					System.out.println("Skipping Round: " + gc.getRoundNumber() + " - " + gc.getTimeLeft() + "ms");
				}
				debugPop(benchmark, 20, "Round Time: " + gc.getRoundNumber() + " - %f/" + gc.getTimeLeft()+"ms");
			} catch (Exception ex) {
				System.out.println("Mystery Exception: " + ex.getMessage());
				ex.printStackTrace();
			}
			gc.yield();
		}
	}
	public static void debugPop(Benchmark benchmark, int threshold, String message) {
		double deltaTime = benchmark.pop() / 1000000.0;
		if (deltaTime > threshold) {
			System.out.println(String.format(message, deltaTime));
		}
	}
}
