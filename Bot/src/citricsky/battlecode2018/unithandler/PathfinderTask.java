package citricsky.battlecode2018.unithandler;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;

public interface PathfinderTask extends Predicate<MapLocation> {
	public default void update() {
		
	}
	public void execute(Unit unit, MapLocation location);
	public Predicate<MapLocation> getStopCondition();
	@Override
	public default boolean test(MapLocation location) {
		return this.getStopCondition().test(location);
	}
}
