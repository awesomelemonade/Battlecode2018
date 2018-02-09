package citricsky.battlecode2018.task;

import citricsky.battlecode2018.library.Unit;

public interface Task {
	public int getPriority(Unit unit);
	public void execute(Unit unit);
}
