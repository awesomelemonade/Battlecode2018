package citricsky.battlecode2018.library;

import java.util.function.Predicate;

public class LibraryUtil {
	public static Unit[] toArray(bc.VecUnit vecUnit) {
		Unit[] units = new Unit[(int) vecUnit.size()];

		for (int i = 0, len = units.length; i < len; ++i) {
			units[i] = new Unit(vecUnit.get(i));
		}

		return units;
	}

	public static Unit[] toFilteredArray(bc.VecUnit vecUnit, Predicate<? super Unit> predicate) {
		Unit[] units = new Unit[(int) vecUnit.size()];

		int offset = 0;
		for (int i = 0, len = units.length; i < len; ++i) {
			Unit unit = new Unit(vecUnit.get(i));
			if (!predicate.test(unit)) {
				++offset;
				break;
			}
			units[i - offset] = unit;
		}

		return units;
	}

	public static int[] toArray(bc.Veci32 vec) {
		int[] array = new int[(int) vec.size()];

		for (int i = 0, len = array.length; i < len; ++i) {
			array[i] = vec.get(i);
		}

		return array;
	}

	public static UnitType[] toArray(bc.VecUnitType vecUnitType) {
		UnitType[] array = new UnitType[(int) vecUnitType.size()];

		for (int i = 0, len = array.length; i < len; ++i) {
			array[i] = UnitType.valueOf(vecUnitType.get(i));
		}

		return array;
	}

	public static int[] toArray(bc.VecUnitID vecUnitId) {
		int[] array = new int[(int) vecUnitId.size()];

		for (int i = 0, len = array.length; i < len; ++i) {
			array[i] = vecUnitId.get(i);
		}

		return array;
	}
	
	public static MapLocation[] toArray(bc.VecMapLocation vecMapLocation) {
		MapLocation[] array = new MapLocation[(int) vecMapLocation.size()];
		
		for(int i = 0, len = array.length; i < len; ++i) {
			array[i] = Planet.getMapLocation(vecMapLocation.get(i));
		}
		
		return array;
	}
	
	public static int[] toArray(bc.VecUnitID vec) {
		int[] array = new int[(int) vec.size()];

		for (int i = 0, len = array.length; i < len; ++i) {
			array[i] = vec.get(i);
		}

		return array;
	}
}
