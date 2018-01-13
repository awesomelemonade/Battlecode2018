package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;

public class MarsPlayer {
	public static void execute() {
		GameController gc = GameController.INSTANCE;

		while(true) {
			System.out.println("R: " + gc.getRoundNumber() + "; K: " + gc.getCurrentKarbonite());
			gc.yield();
		}
	}
}
