package citricsky.battlecode2018.unithandler;

import citricsky.battlecode2018.library.*;

public class RocketHandler implements UnitHandler {
	private Unit unit;

	public RocketHandler(Unit unit) {
		this.unit = unit;
	}

	@Override
	public int getPriority(int priority) {
		if (!unit.isStructureBuilt()) {
			return Integer.MIN_VALUE;
		}
		return Integer.MIN_VALUE + 1;
	}

	@Override
	public void execute() {
		if (unit.getLocation().getMapLocation().getPlanet() == Planet.EARTH) {
			int size = unit.getGarrisonUnitIds().length;
			if (size == 0 || (GameController.INSTANCE.getRoundNumber() < 740 && size < 8)) {
				for (Unit target : unit.senseNearbyUnitsByTeam(2, GameController.INSTANCE.getTeam())) {
					if (target.isStructure()) continue;
					if (unit.canLoad(target)) {
						unit.load(target);
					}
				}
			} else {
				for (int x = (int) (Math.random() * Planet.MARS.getWidth()); x < Planet.MARS.getWidth(); x = (x + 1) % Planet.MARS.getWidth()) {
					for (int y = (int) (Math.random() * Planet.MARS.getHeight()); y < Planet.MARS.getHeight(); y = (y + 1) % Planet.MARS.getHeight()) {
						MapLocation destination = new MapLocation(Planet.MARS, x, y);
						if (destination.isPassableTerrain()) {
							if (unit.canLaunchRocket(destination)) {
								unit.launchRocket(destination);
							}
						}
					}
				}
			}
		} else {
			int garrisonSize = unit.getGarrisonUnitIds().length;
			if (garrisonSize > 0) {
				for (Direction direction : Direction.COMPASS) {
					if (unit.canUnload(direction)) {
						unit.unload(direction);
						if (--garrisonSize == 0) {
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isRequired() {
		return true;
	}
}
