package citricsky.battlecode2018.unithandler;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.MapLocation;

public interface PathfinderTask extends Predicate<MapLocation> {
	public void execute();
	public Predicate<MapLocation> getStopCondition();
	@Override
	public default boolean test(MapLocation location) {
		return this.getStopCondition().test(location);
	}
}
