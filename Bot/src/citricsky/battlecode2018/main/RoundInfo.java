package citricsky.battlecode2018.main;

import java.util.Arrays;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.util.Constants;
import citricsky.battlecode2018.util.Util;

public class RoundInfo {
	private static Planet planet;
	private static int roundNumber;
	private static int[] unitCounts;
	private static int unitCountOnMap;
	private static int combatUnitsCount;
	private static UnitType[] unitTypes;
	private static Unit[] myUnits;
	private static Unit[] enemiesOnMap;
	private static boolean[][] structures;
	static {
		unitCounts = new int[UnitType.values().length];
		unitTypes = new UnitType[Constants.MAX_UNIT_ID];
		planet = GameController.INSTANCE.getPlanet();
		structures = new boolean[planet.getWidth()][planet.getHeight()];
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
		for (int i = 0; i < structures.length; ++i) {
			for (int j = 0; j < structures[0].length; ++j) {
				structures[i][j] = false;
			}
		}
		for (Unit unit: allUnits) {
			unitTypes[unit.getId()] = unit.getType();
			if (unit.getTeam() == GameController.INSTANCE.getTeam()) {
				if (unit.getLocation().isOnMap() && unit.getType().isStructure()) {
					structures[unit.getLocation().getMapLocation().getPosition().getX()]
							[unit.getLocation().getMapLocation().getPosition().getY()] = true;
				}
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
	public static void addStructure(int x, int y) {
		structures[x][y] = true;
	}
	public static boolean hasStructure(int x, int y) {
		if (Util.outOfBounds(x, y, planet.getWidth(), planet.getHeight())) {
			return false;
		}
		return structures[x][y];
	}
	public static UnitType getUnitType(int id) {
		return unitTypes[id];
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
