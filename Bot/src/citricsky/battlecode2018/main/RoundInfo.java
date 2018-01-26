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
	private static Map<Integer, UnitType> unitTypes;
	private static Unit[] myUnits;
	private static Unit[] enemiesOnMap;
	private static Unit[][] units;
	static {
		unitCounts = new int[UnitType.values().length];
		unitTypes = new HashMap<Integer, UnitType>();
	}
	public static void update() {
		unitCountOnMap = 0;
		combatUnitsCount = 0;
		roundNumber = GameController.INSTANCE.getRoundNumber();
		Unit[] allUnits = GameController.INSTANCE.getAllUnits();
		myUnits = new Unit[allUnits.length];
		enemiesOnMap = new Unit[allUnits.length];
		int myUnitsCount = 0;
		int enemiesOnMapCount = 0;
		for (int i = 0; i < units.length; ++i) {
			for (int j = 0; j < units[0].length; ++j) {
				units[i][j] = null;
			}
		}
		for (Unit unit: allUnits) {
			if (!unitTypes.containsKey(unit.getId())) {
				unitTypes.put(unit.getId(), unit.getType());
			}
			if (unit.getLocation().isOnMap()) {
				units[unit.getLocation().getMapLocation().getPosition().getX()]
						[unit.getLocation().getMapLocation().getPosition().getY()] = unit;
			}
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
	public static Unit getUnit(int x, int y) {
		return units[x][y];
	}
	public static UnitType getUnitType(int id) {
		return unitTypes.get(id);
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
