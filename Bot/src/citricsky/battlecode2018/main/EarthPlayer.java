package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.util.Util;

public class EarthPlayer {
	public static void execute() {
		GameController gc = GameController.INSTANCE;

		while(true) {
			System.out.println("R: " + gc.getRoundNumber() + "; K: " + gc.getCurrentKarbonite());
			
			// Workers move first
			for(Unit unit: gc.getMyUnits()) {
				if(unit.getType()==UnitType.WORKER) {
					
				}
			}
			//TODO
			
			gc.yield();
		}
	}
	public static boolean tryBuild(Unit unit, UnitType type) {
		for(Direction direction: Direction.values()) {
			if(unit.canBlueprint(type, direction)) {
				int neighbors = Util.getNeighbors(unit.getLocation(), (mapLocation)->mapLocation.isOccupiable());
				if(Util.canBuild(neighbors)) {
					unit.blueprint(type, direction);
				}
			}
		}
	}
}
