 package citricsky.battlecode2018.library;

public enum UnitType {
	HEALER(bc.UnitType.Healer),
	RANGER(bc.UnitType.Ranger),
	KNIGHT(bc.UnitType.Knight),
	MAGE(bc.UnitType.Mage),
	WORKER(bc.UnitType.Worker),
	ROCKET(bc.UnitType.Rocket),
	FACTORY(bc.UnitType.Factory);

	private bc.UnitType bcUnitType;

	UnitType(bc.UnitType bcUnitType) {
		this.bcUnitType = bcUnitType;
	}

	protected bc.UnitType getBcUnitType(){
		return bcUnitType;
	}

	protected static UnitType valueOf(bc.UnitType bcUnitType) {
		switch(bcUnitType) {
			case Healer:
				return UnitType.HEALER;
			case Ranger:
				return UnitType.RANGER;
			case Knight:
				return UnitType.KNIGHT;
			case Mage:
				return UnitType.MAGE;
			case Worker:
				return UnitType.WORKER;
			case Rocket:
				return UnitType.ROCKET;
			case Factory:
				return UnitType.FACTORY;
			default:
				return null;
		}
	}
}
