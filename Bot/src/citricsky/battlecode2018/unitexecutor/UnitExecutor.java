package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Unit;

public interface UnitExecutor {
	public void execute(Unit unit);
	public default void postExecute(Unit unit) {}
}
