package citricsky.battlecode2018.unithandler;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;

public class FactoryHandler implements UnitHandler {
	private static final UnitType[] PRODUCE = new UnitType[] {UnitType.KNIGHT, UnitType.RANGER};
	private Unit unit;
	
	public FactoryHandler(Unit unit) {
		this.unit = unit;
	}
	
	@Override
	public int getPriority(int priority) {
		if (!unit.isStructureBuilt()) {
			return Integer.MIN_VALUE;
		}
		return Integer.MIN_VALUE+1;
	}
	
	@Override
	public void execute() {
		UnitType randomUnitType = PRODUCE[(int)(Math.random()*PRODUCE.length)];
		if(unit.canProduceRobot(randomUnitType)) {
			unit.produceRobot(randomUnitType);
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
}
