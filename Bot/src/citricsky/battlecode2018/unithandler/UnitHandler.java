package citricsky.battlecode2018.unithandler;

public interface UnitHandler {
	public default int getPriority(int priority) { // Used to cut off unnecessary calculations
		return -Integer.MAX_VALUE;
	}
	public void execute();
	public default boolean isRequired() {
		return false;
	}
}
