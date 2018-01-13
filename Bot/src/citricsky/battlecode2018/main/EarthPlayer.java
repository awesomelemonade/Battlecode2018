package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.util.Util;

public class EarthPlayer {
	public static void execute() {
		GameController gc = GameController.INSTANCE;
		while(true) {
			System.out.println("R: " + gc.getRoundNumber() + "; K: " + gc.getCurrentKarbonite());
			
			for(Unit unit: gc.getMyUnits()) {
				if(unit.getType()==UnitType.WORKER) {
					if(!tryBuild(unit, UnitType.FACTORY)) {
						Direction direction = Direction.randomDirection();
						if(unit.isMoveReady()&&unit.canMove(direction)) {
							unit.move(direction);
						}
					}
					//TODO: Harvesting of Karbonite
				}
				if(unit.getType()==UnitType.FACTORY) {
					if(unit.canProduceRobot(UnitType.KNIGHT)) {
						unit.produceRobot(UnitType.KNIGHT);
					}
					for(Direction direction: Direction.values()) {
						if(unit.canUnload(direction)) {
							unit.unload(direction);
						}
					}
				}
				if(unit.getType()==UnitType.KNIGHT) {
					Direction direction = Direction.randomDirection();
					if(unit.isMoveReady()&&unit.canMove(direction)) {
						unit.move(direction);
					}
				}
			}
			gc.yield();
		}
	}
	public static boolean tryBuild(Unit unit, UnitType type) {
		for(Direction direction: Direction.values()) {
			if(unit.canBlueprint(type, direction)) {
				int neighbors = Util.getNeighbors(unit.getLocation().getMapLocation().getOffsetLocation(direction),
						(mapLocation)->mapLocation.isOccupiable());
				if(Util.canBuild(neighbors)) {
					unit.blueprint(type, direction);
					return true;
				}
			}
		}
		return false;
	}
}
