package citricsky.battlecode2018.library.structure;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.UnitType;

public class Factory extends Structure {
	private int i = 0;
	public Factory(bc.Unit bcUnit) {
		super(bcUnit);
	}

	@Override
	public void act(GameController gc) {
		for (Direction dir : Direction.values()) {
			if (canUnload(dir)) {
				unload(dir);
				break;
			}
		}

		if (canProduceRobot(UnitType.WORKER)) {
			produceRobot(UnitType.WORKER);
			System.out.println("Factory");
		}
	}

	public void produceRobot(UnitType type) {
		super.produceRobot(type);
	}

	public boolean canProduceRobot(UnitType type) {
		return super.canProduceRobot(type);
	}
}
