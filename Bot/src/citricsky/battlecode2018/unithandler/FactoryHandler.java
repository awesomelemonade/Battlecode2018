package citricsky.battlecode2018.unithandler;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;

public class FactoryHandler implements UnitHandler {
	private Unit unit;

	public FactoryHandler(Unit unit) {
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
		UnitType unitType = UnitType.KNIGHT;
		if (unit.senseNearbyUnitsByTeam(10, GameController.INSTANCE.getEnemyTeam()).length > 3) {
			unitType = UnitType.MAGE;
		}
		if(GameController.INSTANCE.getMyUnitsByFilter(unit -> unit.getType() == UnitType.WORKER).length == 0) {
			unitType = UnitType.WORKER;
		}
		if(GameController.INSTANCE.getMyUnitsByFilter(unit -> unit.getType() == UnitType.KNIGHT).length / 3 > 
			GameController.INSTANCE.getMyUnitsByFilter(unit -> unit.getType() == UnitType.HEALER).length) {
			unitType = UnitType.HEALER;
		}
		if (unit.canProduceRobot(unitType)) {
			unit.produceRobot(unitType);
		}
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
