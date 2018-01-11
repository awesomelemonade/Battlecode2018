package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;

public class EarthPlayer {
	public static void execute() {
		GameController gc = GameController.INSTANCE;
		while(true) {
			System.out.println("Current round: " + gc.getRoundNumber());

			for (Unit unit: gc.getMyUnits()) {
				Direction randomDirection = Direction.randomDirection();
				if (unit.isMoveReady() && unit.canMove(randomDirection)) {
					unit.move(randomDirection);
				}
			}

			gc.yield();
		}
	}
}
