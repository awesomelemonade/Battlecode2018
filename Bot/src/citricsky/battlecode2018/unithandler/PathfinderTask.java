package citricsky.battlecode2018.unithandler;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;

public interface PathfinderTask extends Predicate<MapLocation> {
	public default void update() {
		
	}
	public default boolean isActivated(Unit unit) {
		return true;
	}
	public void execute(Unit unit, MapLocation location);
	public boolean isStopCondition(MapLocation location);
	@Override
	public default boolean test(MapLocation location) {
		return this.isStopCondition(location);
	}
}
