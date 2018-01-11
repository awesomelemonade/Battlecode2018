package citricsky.battlecode2018.library.Structure;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;

public abstract class Structure extends Unit {
	public Structure(bc.Unit bcUnit) {
		super(bcUnit);
	}

	public abstract void act(GameController gc);

}