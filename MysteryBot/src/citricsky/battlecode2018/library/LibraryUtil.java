package citricsky.battlecode2018.library;

import java.util.Arrays;
import java.util.function.Predicate;

public class LibraryUtil {
	public static Unit[] toArray(bc.VecUnit vecUnit) {
		if (vecUnit == null) return new Unit[0];
		Unit[] units = new Unit[(int) vecUnit.size()];

		for (int i = 0, len = units.length; i < len; ++i) {
			units[i] = new Unit(vecUnit.get(i));
		}

		return units;
	}

	public static Unit[] toFilteredArray(bc.VecUnit vecUnit, Predicate<? super Unit> predicate) {
		if (vecUnit == null) return new Unit[0];
		Unit[] units = new Unit[(int) vecUnit.size()];

		int offset = 0;
		for (int i = 0, len = units.length; i < len; ++i) {
			Unit unit = new Unit(vecUnit.get(i));
			if (!predicate.test(unit)) {
				++offset;
				continue;
			}
			units[i - offset] = unit;
		}

		return Arrays.copyOfRange(units, 0, (int) vecUnit.size() - offset);
	}

	public static int[] toArray(bc.Veci32 vec) {
		if (vec == null) return new int[0];
		int[] array = new int[(int) vec.size()];

		for (int i = 0, len = array.length; i < len; ++i) {
			array[i] = vec.get(i);
		}

		return array;
	}

	public static UnitType[] toArray(bc.VecUnitType vecUnitType) {
		if (vecUnitType == null) return new UnitType[0];
		UnitType[] array = new UnitType[(int) vecUnitType.size()];

		for (int i = 0, len = array.length; i < len; ++i) {
			array[i] = UnitType.valueOf(vecUnitType.get(i));
		}

		return array;
	}

	public static int[] toArray(bc.VecUnitID vecUnitId) {
		if (vecUnitId == null) return new int[0];
		int[] array = new int[(int) vecUnitId.size()];

		for (int i = 0, len = array.length; i < len; ++i) {
			array[i] = vecUnitId.get(i);
		}

		return array;
	}
	
	public static MapLocation[] toArray(bc.VecMapLocation vecMapLocation) {
		if (vecMapLocation == null) return new MapLocation[0];
		MapLocation[] array = new MapLocation[(int) vecMapLocation.size()];
		
		for(int i = 0, len = array.length; i < len; ++i) {
			array[i] = Planet.getMapLocation(vecMapLocation.get(i));
		}
		
		return array;
	}
}
