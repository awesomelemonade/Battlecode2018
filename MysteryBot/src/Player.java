import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.main.PlanetCommunication;
import citricsky.battlecode2018.util.AllPairs;
import citricsky.battlecode2018.util.Benchmark;
import citricsky.battlecode2018.util.RoundInfo;
import citricsky.battlecode2018.util.Util;

public class Player {
	public static void main(String[] args) {
		Util.init();
		Benchmark benchmark = new Benchmark();
		GameController.INSTANCE.init(); //Explicit Initialization
		GameController gc = GameController.INSTANCE;
		if (gc.getPlanet() == Planet.EARTH) {
			UnitType[] researchOrder = new UnitType[]{
					UnitType.WORKER, //finishes at Round 25
					UnitType.HEALER, //finishes at Round 50
					UnitType.HEALER, //finishes at Round 150
					UnitType.KNIGHT, //finishes at Round 175
					UnitType.ROCKET, //finishes at Round 225
					UnitType.RANGER, //finishes at Round 250
					UnitType.RANGER, //finishes at Round 350
					UnitType.RANGER, //finishes at Round 550
					UnitType.KNIGHT, //finishes at Round 625
					UnitType.KNIGHT, //finishes at Round 725
					UnitType.MAGE,   //finishes at Round 750
					UnitType.HEALER, //finishes at Round 850
			};
			for (UnitType research: researchOrder) {
				gc.queueResearch(research);
			}
		}
		benchmark.push();
		AllPairs.init(gc.getPlanet());
		debugPop(benchmark, 0, "AllPairs Time: %fms");
		
		PlanetCommunication pc = new PlanetCommunication();
		while (true) {
			try {
				benchmark.push();
				RoundInfo.update();
				pc.update();
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
