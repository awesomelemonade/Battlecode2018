package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.main.MoveManager;

public class FactoryExecutor implements UnitExecutor {
	private MoveManager moveManager;
	
	public FactoryExecutor(MoveManager moveManager) {
		this.moveManager = moveManager;
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void execute(Unit unit) {
		
		if (unit.canProduceRobot(UnitType.RANGER)) { //more logic here
			unit.produceRobot(UnitType.RANGER);
		}
		
		if (unit.getGarrisonUnitIds().length > 0) {
			Direction bestUnloadDirection = null;
			int closestEnemy = Integer.MAX_VALUE;
			for (Direction direction: Direction.COMPASS) {
				if (unit.canUnload(direction)) {
					Vector position = unit.getLocation().getMapLocation().getPosition().add(direction.getOffsetVector());
					int bfsStep = moveManager.getBFSStep(MoveManager.BFS_FIND_ENEMY, position);
					if (bfsStep < closestEnemy) {
						closestEnemy = bfsStep;
						bestUnloadDirection = direction;
					}
				}
			}
			if (bestUnloadDirection != null) {
				unit.canUnload(bestUnloadDirection);
			}
		}
	}
}
