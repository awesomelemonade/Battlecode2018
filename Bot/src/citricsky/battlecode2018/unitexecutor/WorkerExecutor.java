package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.main.MoveManager;
import citricsky.battlecode2018.util.Constants;

public class WorkerExecutor implements UnitExecutor {
	private MoveManager moveManager;
	public WorkerExecutor(MoveManager moveManager) {
		this.moveManager = moveManager;
	}
	@Override
	public void update() {
		
	}
	private boolean shouldReplicate() {
		return GameController.INSTANCE.getCurrentKarbonite() > Constants.WORKER_REPLICATE_COST;
	}
	@Override
	public void execute(Unit unit) {
		if (shouldReplicate()) {
			Direction bestReplicateDirection = null;
			int closestTask = Integer.MAX_VALUE;
			for (Direction direction: Direction.COMPASS) {
				if (unit.canReplicate(direction)) {
					Vector position = unit.getLocation().getMapLocation().getPosition().add(direction.getOffsetVector());
					int bfsStep = moveManager.getBFSStep(MoveManager.BFS_WORKER, position);
					if (bfsStep < closestTask) {
						closestTask = bfsStep;
						bestReplicateDirection = direction;
					}
				}
			}
			if (bestReplicateDirection != null) {
				unit.replicate(bestReplicateDirection);
			}
		}
		//try build
		//try repair
		//try harvest
		//try blueprint
	}
}
