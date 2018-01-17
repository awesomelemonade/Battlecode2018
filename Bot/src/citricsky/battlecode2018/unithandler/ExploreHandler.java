package citricsky.battlecode2018.unithandler;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.main.BFS;
import citricsky.battlecode2018.util.Util;

public class ExploreHandler implements UnitHandler {
	private Unit unit;
	public ExploreHandler(Unit unit) {
		this.unit = unit;
	}
	@Override
	public void execute() {
		if (!unit.getLocation().isOnMap()) return;
		if (!unit.isMoveReady()) return;
		
		
		BFS bfs = new BFS(unit.getLocation().getMapLocation());
		
		bfs.process(Util.PASSABLE_PREDICATE, location -> !GameController.INSTANCE.canSenseLocation(location));
		
		if (bfs.getStopLocation() != null) {
			int directions = bfs.getDirectionFromSource(bfs.getStopLocation().getPosition());
			for(Direction direction: Direction.COMPASS) {
				if(((directions >>> direction.ordinal()) & 1) == 1) {
					if(unit.canMove(direction)) {
						unit.move(direction);
						break;
					}
				}
			}
		}
	}
	@Override
	public int getPriority(int priority) {
		return -Integer.MAX_VALUE + 1;
	}
}
