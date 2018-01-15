package citricsky.battlecode2018.unithandler;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.Unit;

public class ExploreHandler implements UnitHandler {
	private Unit unit;
	public ExploreHandler(Unit unit) {
		this.unit = unit;
	}
	@Override
	public void execute() {
		for (Direction direction: Direction.COMPASS) {
			if (unit.isMoveReady() && unit.canMove(direction)) {
				unit.move(direction);
			}
		}
	}
	@Override
	public int getPriority(int priority) {
		return -Integer.MAX_VALUE + 1;
	}
}