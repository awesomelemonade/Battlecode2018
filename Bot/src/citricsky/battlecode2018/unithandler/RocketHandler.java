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
		int garrisonSize = unit.getGarrisonUnitIds().length;
		if (unit.getLocation().getMapLocation().getPlanet() == Planet.EARTH) {
			if (garrisonSize == 0 || (GameController.INSTANCE.getRoundNumber() < 740 && garrisonSize < 8)) {
				for (Unit target : unit.senseNearbyUnitsByTeam(2, GameController.INSTANCE.getTeam())) {
					if (target.isStructure()) continue;
					if (unit.canLoad(target)) {
						unit.load(target);
					}
				}
			}
		} else {
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
