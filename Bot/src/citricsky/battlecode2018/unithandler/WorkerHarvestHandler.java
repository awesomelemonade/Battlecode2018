package citricsky.battlecode2018.unithandler;

import citricsky.battlecode2018.library.Unit;

public class WorkerHarvestHandler implements UnitHandler {
	
	public WorkerHarvestHandler(Unit unit) {
		
	}
	
	@Override
	public int getPriority(int priority) {
		return -Integer.MAX_VALUE;
	}

	@Override
	public void execute() {
		
	}
}
