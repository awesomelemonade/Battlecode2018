package citricsky.battlecode2018.unithandler;

public interface UnitHandler {
	public int getPriority(int priority); // Used to cut off unnecessary calculations
	public void execute();
}
