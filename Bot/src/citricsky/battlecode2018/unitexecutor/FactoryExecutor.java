package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Direction;
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
	
	public int getCombatUnitsCount() {
		int total = 0;
		for (UnitType type: UnitType.values()) {
			if (type.isCombatType()) {
				total += RoundInfo.getUnitCount(type);
			}
		}
		return total;
	}
	
	public UnitType getProduceType() {
		if (RoundInfo.getRoundNumber() > 650) {
			if (RoundInfo.getUnitCount(UnitType.WORKER) < 10) {
				return UnitType.WORKER;
			}
		}
		if (RoundInfo.getUnitCount(UnitType.WORKER) * 2 - 6 < RoundInfo.getUnitCount(UnitType.FACTORY)) {
			return UnitType.WORKER;
		}
		if (RoundInfo.getUnitCount(UnitType.HEALER) + 1 < getCombatUnitsCount() / 4) {
			return UnitType.HEALER;
		}
		return UnitType.RANGER;
	}
	
	@Override
	public void execute(Unit unit) {
		UnitType produceType = getProduceType();
		if (produceType != null && unit.canProduceRobot(produceType)) {
			unit.produceRobot(produceType);
		}
		int garrisonSize = unit.getGarrisonUnitIds().length;
		for (int i = 0; i < garrisonSize; ++i) {
			Direction bestUnloadDirection = null;
			int closestEnemy = Integer.MAX_VALUE;
			for (Direction direction: Direction.COMPASS) {
				if (unit.canUnload(direction)) {
					Vector position = unit.getLocation().getMapLocation().getPosition().add(direction.getOffsetVector());
					int bfsStep = moveManager.getBFSStep(MoveManager.BFS_FIND_ALL_ENEMY, position) - 1;
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
