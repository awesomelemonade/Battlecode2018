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
		return Integer.MAX_VALUE;
	}

	@Override
	public void execute() {
		if (unit.getLocation().getMapLocation().getPlanet() == Planet.EARTH) {
			if (unit.getGarrisonUnitIds().length == unit.getStructureMaxCapacity() ||
					GameController.INSTANCE.getRoundNumber() == 749 ||
					((double)unit.getHealth()) / ((double)unit.getMaxHealth()) < 0.5) {
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