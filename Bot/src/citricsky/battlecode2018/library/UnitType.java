 package citricsky.battlecode2018.library;

public enum UnitType {
	WORKER  (bc.UnitType.Worker,   50, 100,   0,  0, 50, 20),
	KNIGHT  (bc.UnitType.Knight,   40, 250,  80,  2, 50, 15),
	RANGER  (bc.UnitType.Ranger,   40, 200,  30, 50, 70, 30),
	MAGE    (bc.UnitType.Mage,     40,  80,  60, 30, 30, 20),
	HEALER  (bc.UnitType.Healer,   40, 100, -10, 30, 50, 25),
	ROCKET  (bc.UnitType.Rocket,  150, 200,   0,  0,  2, 0),
	FACTORY (bc.UnitType.Factory, 200, 300,   0,  0,  2, 0);

	private bc.UnitType bcUnitType;
	private int baseCost;
	private int baseHealth;
	private int baseDamage;
	private int baseAttackRange;
	private int baseVisionRange;
	private int baseMovementCooldown;

	UnitType(bc.UnitType bcUnitType, int baseCost, int baseHealth, int baseDamage, int baseAttackRange, int baseVisionRange, int baseMovementCooldown) {
		this.bcUnitType = bcUnitType;
		this.baseCost = baseCost;
		this.baseHealth = baseHealth;
		this.baseDamage = baseDamage;
		this.baseAttackRange = baseAttackRange;
		this.baseVisionRange = baseVisionRange;
		this.baseMovementCooldown = baseMovementCooldown;
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
	public int getBaseMovementCooldown() {
		return baseMovementCooldown;
	}
	public boolean isStructure() {
		return UnitType.isStructure(this);
	}
	public boolean isCombatType() {
		return UnitType.isCombatType(this);
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
	
	public static boolean isStructure(UnitType type) {
		return type == UnitType.ROCKET || type == UnitType.FACTORY;
	}
	
	public static boolean isCombatType(UnitType type) {
		return type == UnitType.KNIGHT || type == UnitType.RANGER || type == UnitType.HEALER || type == UnitType.MAGE;
	}
}
