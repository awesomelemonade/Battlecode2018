package citricsky.battlecode2018.main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;

public class RoundInfo {
	private static int roundNumber;
	private static int[] unitCounts;
	private static int unitCountOnMap;
	private static int combatUnitsCount;
	private static Map<Integer, Unit> unitsById;
	private static Unit[] myUnits;
	private static Unit[] enemiesOnMap;
	static {
		unitCounts = new int[UnitType.values().length];
		unitsById = new HashMap<Integer, Unit>();
	}
	public static void update() {
		unitCountOnMap = 0;
		unitsById.clear();
		roundNumber = GameController.INSTANCE.getRoundNumber();
		Unit[] allUnits = GameController.INSTANCE.getAllUnits();
		myUnits = new Unit[allUnits.length];
		enemiesOnMap = new Unit[allUnits.length];
		int myUnitsCount = 0;
		int enemiesOnMapCount = 0;
		for (Unit unit: allUnits) {
			unitsById.put(unit.getId(), unit);
			if (unit.getTeam() == GameController.INSTANCE.getTeam()) {
				myUnits[myUnitsCount++] = unit;
			} else {
				if (unit.getLocation().isOnMap()) {
					enemiesOnMap[enemiesOnMapCount++] = unit;
				}
			}
		}
		myUnits = Arrays.copyOfRange(myUnits, 0, myUnitsCount);
		enemiesOnMap = Arrays.copyOfRange(enemiesOnMap, 0, enemiesOnMapCount);
		for (int i = 0; i < unitCounts.length; ++i) {
			unitCounts[i] = 0;
		}
		for (Unit unit: myUnits) {
			unitCounts[unit.getType().ordinal()]++;
			if (unit.getLocation().isOnMap()) {
				unitCountOnMap++;
				if (unit.getType().isCombatType()) {
					combatUnitsCount++;
				}
			}
		}
	}
	public static Unit getUnit(int id) {
		return unitsById.get(id);
	}
	public static int getUnitCountOnMap() {
		return unitCountOnMap;
	}
	public static int getCombatUnitsCount() {
		return combatUnitsCount;
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
