package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;

public class RoundInfo {
	private static int roundNumber;
	private static Unit[] myUnits;
	private static Unit[] enemiesOnMap;
	private static int[] unitCounts;
	private static int unitCountOnMap;
	static {
		unitCounts = new int[UnitType.values().length];
	}
	public static void update() {
		unitCountOnMap = 0;
		roundNumber = GameController.INSTANCE.getRoundNumber();
		myUnits = GameController.INSTANCE.getMyUnits();
		enemiesOnMap = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam() &&
						unit.getLocation().isOnMap()
		);
		for (int i = 0; i < unitCounts.length; ++i) {
			unitCounts[i] = 0;
		}
		for (Unit unit: myUnits) {
			unitCounts[unit.getType().ordinal()]++;
			if (unit.getLocation().isOnMap()) {
				unitCountOnMap++;
			}
		}
	}
	public static int getUnitCountOnMap() {
		return unitCountOnMap;
	}
	public static Unit[] getMyUnits() {
		return myUnits;
	}
	public static Unit[] getEnemiesOnMap() {
		return enemiesOnMap;
	}
	public static int getRoundNumber() {
		return roundNumber;
	}
	public static int getUnitCount(UnitType type) {
		return unitCounts[type.ordinal()];
	}
	public static int[] getUnitCounts() {
		return unitCounts;
	}
}
