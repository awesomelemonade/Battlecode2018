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
		UnitType[] researchOrder = new UnitType[]
				{UnitType.WORKER, //finishes at Round 25
				 UnitType.HEALER, //finishes at Round 50
				 UnitType.RANGER, //finishes at Round 75
				 UnitType.HEALER, //finishes at Round 175
				 UnitType.ROCKET, //finishes at Round 225
				 UnitType.RANGER, //finishes at Round 325
				 UnitType.RANGER, //finishes at Round 525
				 UnitType.ROCKET, //finishes at Round 625
				 UnitType.ROCKET, //finishes at Round 725
				 UnitType.HEALER, //finishes at Round 825
				 }; 
		/*UnitType[] researchOrder = new UnitType[] {
				UnitType.WORKER, UnitType.HEALER, UnitType.RANGER, UnitType.HEALER,
				UnitType.HEALER, UnitType.HEALER, UnitType.RANGER, UnitType.RANGER
		};*/
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
			try {
				benchmark.push();
				if (benchmark.peek() / 1000000 < gc.getTimeLeft() - 2000) {
					RoundInfo.update();
					communication.update();
					if (benchmark.peek() / 1000000 < gc.getTimeLeft() - 3000) {
						try {
							moveManager.updateBFS();
						} catch (Exception ex) {
							System.out.println("BFS Exception: "+ex.getMessage());
						}
					} else {
						System.out.println("Skipping BFS Update");
					}
					moveManager.move(unit -> {
						try {
							if(executors[unit.getType().ordinal()] != null) {
								executors[unit.getType().ordinal()].execute(unit);
							}
						} catch (Exception ex) {
							System.out.println("Execution Exception: "+ ex.getMessage());
							ex.printStackTrace();
						}
					});
				} else {
					System.out.println("Skipping Round: " + gc.getRoundNumber() + " - " + gc.getTimeLeft() + "ms");
				}
				double deltaTime = benchmark.pop() / 1000000.0;
				if(deltaTime > 20) {
					System.out.println("Round Time: " + gc.getRoundNumber() + " - " + deltaTime + "/" + gc.getTimeLeft() + "ms");
				}
			} catch (Exception ex) {
				System.out.println("Mystery Exception: "+ex.getMessage());
				ex.printStackTrace();
			}
			gc.yield();
		}
	}
}
