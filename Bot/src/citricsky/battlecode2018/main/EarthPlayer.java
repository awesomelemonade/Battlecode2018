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
			
			for(Unit unit: gc.getMyUnits()) {
				if(unit.getType()==UnitType.WORKER) {
					if(!tryBuild(unit, UnitType.FACTORY)) {
						Direction direction = Direction.randomDirection();
						if(unit.canMove(direction)&&unit.isMoveReady()) {
							unit.move(direction);
						}
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
		if(unit.isMoveReady()) {
			for(Direction moveDirection: Direction.values()) {
				if(!unit.canMove(moveDirection)) {
					continue;
				}
				for(Direction blueprintDirection: Direction.values()) {
					MapLocation proposal = unit.getLocation().getMapLocation()
							.getOffsetLocation(moveDirection).getOffsetLocation(blueprintDirection);
					if(!proposal.isOccupiable()) {
						continue;
					}
					int neighbors = Util.getNeighbors(proposal, (mapLocation)->mapLocation.isOccupiable());
					if(Util.canBuild(neighbors)) {
						unit.move(moveDirection);
						unit.blueprint(type, blueprintDirection);
						return true;
					}
				}
			}
		}
		return false;
	}
}
