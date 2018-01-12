package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.robot.Robot;
import citricsky.battlecode2018.library.structure.Factory;
import citricsky.battlecode2018.library.Unit;

public class MarsPlayer {
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
