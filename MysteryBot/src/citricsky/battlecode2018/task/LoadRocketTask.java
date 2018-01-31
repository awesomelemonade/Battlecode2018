package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.util.Constants;

public class LoadRocketTask implements Task {
	private BFS[] bfsCache;
	private Vector[] cache;
	
	public LoadRocketTask() {
		bfsCache = new BFS[]
		cache = new Vector[Constants.MAX_UNIT_ID];
	}
	
	@Override
	public int getPriority(Unit unit) {
		return 0;
	}

	@Override
	public void execute(Unit unit) {
		Vector target = cache[unit.getId()];
	}
}
