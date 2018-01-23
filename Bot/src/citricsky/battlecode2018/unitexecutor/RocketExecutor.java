package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.main.MoveManager;

public class RocketExecutor implements UnitExecutor {
	private MoveManager moveManager;
	public RocketExecutor(MoveManager moveManager) {
		this.moveManager = moveManager;
	}
	@Override
	public void execute(Unit unit) {
		if (unit.getLocation().getMapLocation().getPlanet() == Planet.EARTH) {
			if (unit.getGarrisonUnitIds().length == unit.getStructureMaxCapacity() ||
					((double)unit.getHealth()) / ((double)unit.getMaxHealth()) < 0.5 || 
						GameController.INSTANCE.getRoundNumber() > 740) {
				int offsetX = (int)(Math.random() * Planet.MARS.getWidth());
				int offsetY = (int)(Math.random() * Planet.MARS.getHeight());
				for(int x = 0; x < Planet.MARS.getWidth(); x++) {
					for(int y = 0; y < Planet.MARS.getHeight(); y++) {
						MapLocation destination = Planet.MARS.getMapLocation(
								(x + offsetX) % Planet.MARS.getWidth(), (y + offsetY) % Planet.MARS.getHeight());
						if(destination.isPassableTerrain()) {
							if (unit.canLaunchRocket(destination)) {
								unit.launchRocket(destination);
								return;
							}
						}
					}
				}
			
			} else {
				for (Unit target : unit.senseNearbyUnitsByTeam(2, GameController.INSTANCE.getTeam())) {
					if (target.isStructure()) continue;
					if (unit.canLoad(target)) {
						unit.load(target);
					}
				}
			}
		} else {
			if (unit.getGarrisonUnitIds().length > 0) {
				Direction bestUnloadDirection = null;
				int closestEnemy = Integer.MAX_VALUE;
				for (Direction direction: Direction.COMPASS) {
					if (unit.canUnload(direction)) {
						Vector position = unit.getLocation().getMapLocation().getPosition().add(direction.getOffsetVector());
						int bfsStep = moveManager.getBFSStep(MoveManager.BFS_FIND_ENEMY, position) - 1;
						if (bfsStep < closestEnemy) {
							closestEnemy = bfsStep;
							bestUnloadDirection = direction;
						}
					}
				}
				if (bestUnloadDirection != null) {
					unit.unload(bestUnloadDirection);
				}
			}
		}
	}
}
