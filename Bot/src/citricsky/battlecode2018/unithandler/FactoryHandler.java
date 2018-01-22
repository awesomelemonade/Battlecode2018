package citricsky.battlecode2018.unithandler;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.main.RoundInfo;

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
		UnitType unitType = UnitType.RANGER;
		if (unit.senseNearbyUnitsByTeam(10, GameController.INSTANCE.getEnemyTeam()).length > 0) {
			unitType = UnitType.KNIGHT;
		}
		if(RoundInfo.getUnitCount(UnitType.WORKER) == 0) {
			unitType = UnitType.WORKER;
		}
		if(GameController.INSTANCE.getCurrentKarbonite() >= unitType.getBaseCost()){
			if (unit.canProduceRobot(unitType)) {
				unit.produceRobot(unitType);
			}
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