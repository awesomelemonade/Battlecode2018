package citricsky.battlecode2018.library;

import citricsky.battlecode2018.library.Robot.*;
import citricsky.battlecode2018.library.Structure.*;

public class Util {
	public static Unit[] toArray(bc.VecUnit vecUnit) {
		Unit[] units = new Unit[(int) vecUnit.size()];

		for (int i = 0, len = units.length; i < len; ++i) {
			switch(UnitType.valueOf(vecUnit.get(i).unitType())) {
				case WORKER:
					units[i] = new Worker(vecUnit.get(i));
					break;
				case FACTORY:
					units[i] = new Factory(vecUnit.get(i));
					break;
				default:
					units[i] = new Unit(vecUnit.get(i));
			}
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
