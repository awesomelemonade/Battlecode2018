package citricsky.battlecode2018.library.Robot;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;

public abstract class Robot extends Unit {
	public Robot(bc.Unit bcUnit) {
		super(bcUnit);
	}

	public abstract void act(GameController gc);

}