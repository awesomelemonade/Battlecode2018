package citricsky.battlecode2018.unithandler;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.Unit;

public class FactoryUnloadHandler implements UnitHandler {
	private Unit unit;
	
	public FactoryUnloadHandler(Unit unit) {
		this.unit = unit;
	}
	
	@Override
	public int getPriority(int priority) {
		return -Integer.MAX_VALUE+1;
	}
	
	@Override
	public void execute() {
		int garrisonSize = unit.getGarrisonUnitIds().length;
		if (garrisonSize > 0) {
			for (Direction direction : Direction.values()) {
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
}
