package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Robot.Robot;
import citricsky.battlecode2018.library.Structure.Factory;
import citricsky.battlecode2018.library.Unit;

public class EarthPlayer {
	public static void execute() {
		GameController gc = GameController.INSTANCE;

		while(true) {
			System.out.println("R: " + gc.getRoundNumber() + "; K: " + gc.getCurrentKarbonite());

			for (Unit unit: gc.getMyUnits()) {
				if (unit instanceof Robot) {
					((Robot) unit).act(gc);
				} else if (unit instanceof Factory) {
					((Factory) unit).act(gc);
				}

			}

			gc.yield();
		}
	}
}
