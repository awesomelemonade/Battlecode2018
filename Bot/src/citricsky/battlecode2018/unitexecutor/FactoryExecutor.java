package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.main.MoveManager;
import citricsky.battlecode2018.main.RoundInfo;

public class FactoryExecutor implements UnitExecutor {
	private MoveManager moveManager;
	
	public FactoryExecutor(MoveManager moveManager) {
		this.moveManager = moveManager;
	}
	
	public UnitType getProduceType(MapLocation location) {
		if (RoundInfo.getRoundNumber() > 650) {
			if (RoundInfo.getUnitCount(UnitType.WORKER) < 10) {
				return UnitType.WORKER;
			}
		}
		if (moveManager.nearEnemy(location.getPosition(), 10, true)) {
			return UnitType.KNIGHT;
		}
		if (RoundInfo.getUnitCount(UnitType.WORKER) * 2 - 6 < RoundInfo.getUnitCount(UnitType.FACTORY)) {
			return UnitType.WORKER;
		}
		if ((float)RoundInfo.getUnitCount(UnitType.HEALER) < (float)RoundInfo.getCombatUnitsCount() / 2.5) {
			return UnitType.HEALER;
		}
		return UnitType.RANGER;
	}
	
	@Override
	public void execute(Unit unit) {
		if (!unit.isStructureBuilt()) {
			return;
		}
		UnitType produceType = getProduceType(unit.getLocation().getMapLocation());
		if (produceType != null && (produceType == UnitType.WORKER || RoundInfo.getCombatUnitsCount() < 50) && unit.canProduceRobot(produceType)) {
			unit.produceRobot(produceType);
		}
		int[] garrison = unit.getGarrisonUnitIds();
		for (int i = 0; i < garrison.length; ++i) {
			Direction bestUnloadDirection = null;
			int closestEnemy = Integer.MAX_VALUE;
			for (Direction direction: Direction.COMPASS) {
				if (unit.canUnload(direction)) {
					Vector position = unit.getLocation().getMapLocation().getPosition().add(direction.getOffsetVector());
					int bfsIndex = moveManager.getBFSIndex(RoundInfo.getUnitType(garrison[i]), Planet.EARTH, position, 1.0);
					int bfsStep = moveManager.getBFSStep(bfsIndex, position) - 1;
					if (closestEnemy == Integer.MAX_VALUE || bfsStep < closestEnemy) {
						closestEnemy = bfsStep;
						bestUnloadDirection = direction;
					}
				}
			}
			if (bestUnloadDirection == null) {
				break;
			} else {
				unit.unload(bestUnloadDirection);
			}
		}
	}
}
