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
		UnitType unitType = UnitType.RANGER;
		if (unit.senseNearbyUnitsByTeam(10, GameController.INSTANCE.getEnemyTeam()).length > 0) {
			unitType = UnitType.KNIGHT;
		}
		if(GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getTeam() && unit.getType() == UnitType.WORKER).length == 0) {
			unitType = UnitType.WORKER;
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

	@Override
	public boolean isRequired() {
		return true;
	}

	enum Production {
		KNIGHT(UnitType.KNIGHT, 0, 20),
		RANGER(UnitType.RANGER, 20, 75),
		MAGE(UnitType.MAGE, 75, 85),
		HEALER(UnitType.HEALER, 85, 95),
		WORKER(UnitType.WORKER, 95, 100);

		private UnitType type;
		private byte probMin; // 0 to 100 (inclusive)
		private byte probMax;

		private static int total = -1;

		Production(UnitType type, int probMin, int probMax) {
			this.type = type;
			this.probMin = (byte) probMin;
			this.probMax = (byte) probMax;
		}

		public UnitType getType() {
			return type;
		}

		public static UnitType getFromProb(int prob) {
			for (Production prod : Production.values()) {
				if (prod.probMin > prob) continue;
				if (prod.probMax < prob) continue;
				return prod.getType();
			}
			return null;
		}
	}
}
