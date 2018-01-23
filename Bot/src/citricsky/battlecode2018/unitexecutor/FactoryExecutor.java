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
	
	@Override
	public void update() {
		
	}
	
	public UnitType getProduceType() {
		if (RoundInfo.getUnitCount(UnitType.WORKER) == 0) {
			return UnitType.WORKER;
		}
		if (RoundInfo.getUnitCount(UnitType.HEALER) + 1 < RoundInfo.getMyUnits().length / 6) {
			return UnitType.HEALER;
		}
		return UnitType.RANGER;
	}
	
	@Override
	public void execute(Unit unit) {
		if (unit.canProduceRobot(getProduceType())) { //more logic here
			unit.produceRobot(getProduceType());
		}
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
