package citricsky.battlecode2018.library;

public class Util {
	public static Unit[] toArray(bc.VecUnit vecUnit) {
		Unit[] units = new Unit[(int) vecUnit.size()];

		for (int i = 0, len = units.length; i < len; ++i) {
			units[i] = new Unit(vecUnit.get(i));
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
}
