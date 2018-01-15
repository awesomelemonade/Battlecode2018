package citricsky.battlecode2018.unithandler;

public interface UnitHandler {
	public default int getPriority(int priority) {
		return -Integer.MAX_VALUE;
	}
	public void execute();
	public default boolean isRequired() {
		return false;
	}
}
