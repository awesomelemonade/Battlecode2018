 package citricsky.battlecode2018.library;

public enum UnitType {
	WORKER(bc.UnitType.Worker, 25, 100, 0, 0, 50),
	KNIGHT(bc.UnitType.Knight, 20, 250, 60, 1, 50),
	RANGER(bc.UnitType.Ranger, 20, 200, 40, 50, 70),
	MAGE(bc.UnitType.Mage, 20, 80, 60, 30, 30),
	HEALER(bc.UnitType.Healer, 20, 100, -10, 30, 50),
	ROCKET(bc.UnitType.Rocket, 75, 200, 0, 0, 2),
	FACTORY(bc.UnitType.Factory, 100, 300, 0, 0, 2);
		

	private bc.UnitType bcUnitType;
	private int baseCost;
	private int baseHealth;
	private int baseDamage;
	private int baseAttackRange;
	private int baseVisionRange;

	UnitType(bc.UnitType bcUnitType, int baseCost, int baseHealth, int baseDamage, int baseAttackRange, int baseVisionRange) {
		this.bcUnitType = bcUnitType;
		this.baseCost = baseCost;
		this.baseHealth = baseHealth;
		this.baseDamage = baseDamage;
		this.baseAttackRange = baseAttackRange;
		this.baseVisionRange = baseVisionRange;
	}

	protected bc.UnitType getBcUnitType(){
		return bcUnitType;
	}
	public int getBaseCost() {
		return baseCost;
	}
	public int getBaseHealth() {
		return baseHealth;
	}
	public int getBaseDamage() {
		return baseDamage;
	}
	public int getBaseAttackRange() {
		return baseAttackRange;
	}
	public int getBaseVisionRange() {
		return baseVisionRange;
	}
	

	protected static UnitType valueOf(bc.UnitType bcUnitType) {
		switch(bcUnitType) {
			case Worker:
				return UnitType.WORKER;
			case Knight:
				return UnitType.KNIGHT;
			case Ranger:
				return UnitType.RANGER;
			case Mage:
				return UnitType.MAGE;
			case Healer:
				return UnitType.HEALER;
			case Rocket:
				return UnitType.ROCKET;
			case Factory:
				return UnitType.FACTORY;
			default:
				return null;
		}
	}
}
