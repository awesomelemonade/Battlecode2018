package citricsky.battlecode2018.library;

import java.util.function.Predicate;

public class Unit {
	protected bc.Unit bcUnit;
	
	private int id;
	private Team team;
	private UnitType type;
	private Location location;
	
	private GameController gcInstance = GameController.INSTANCE;
	
	protected Unit(bc.Unit bcUnit) {
		this.bcUnit = bcUnit;
		//Initialize
		this.id = bcUnit.id();
		this.team = Team.valueOf(bcUnit.team());
		this.type = UnitType.valueOf(bcUnit.unitType());
		this.location = new Location(bcUnit.location());
	}
	
	public int getAbilityCooldown() {
		return (int)bcUnit.abilityCooldown();
	}
	
	public int getAbilityHeat() {
		return (int)bcUnit.abilityHeat();
	}
	
	public int getAbilityRange() {
		return (int)bcUnit.abilityRange();
	}
	
	public int getAttackCooldown() {
		return (int)bcUnit.attackCooldown();
	}
	
	public int getAttackHeat() {
		return (int)bcUnit.attackHeat();
	}
	
	public int getAttackRange() {
		return (int)bcUnit.attackRange();
	}
	
	public int getDamage() {
		return (int)bcUnit.damage();
	}
	
	public int getFactoryMaxRoundsLeft() {
		return (int)bcUnit.factoryMaxRoundsLeft();
	}
	
	public int getFactoryRoundsLeft() {
		return (int)bcUnit.factoryRoundsLeft();
	}
	
	public UnitType getFactoryUnitType() {
		return UnitType.valueOf(bcUnit.factoryUnitType());
	}
	
	public int getHealerSelfHealAmount() {
		return (int)bcUnit.healerSelfHealAmount();
	}
	
	public boolean isAbilityUnlocked() {
		return bcUnit.isAbilityUnlocked() > 0;
	}
	
	public boolean isFactoryProducing() {
		return bcUnit.isFactoryProducing() > 0;
	}
	
	public int getKnightDefense() {
		return (int)bcUnit.knightDefense();
	}
	
	public int getMovementCooldown() {
		return (int)bcUnit.movementCooldown();
	}
	
	public int getMovementHeat() {
		return (int)bcUnit.movementHeat();
	}
	
	public int getRangerCannotAttackRange() {
		return (int)bcUnit.rangerCannotAttackRange();
	}
	
	public int getRangerCountdown() {
		return (int)bcUnit.rangerCountdown();
	}
	
	public boolean isRangerSniping() {
		return bcUnit.rangerIsSniping()>0;
	}
	
	public int getRangerMaxCooldown() {
		return (int)bcUnit.rangerMaxCountdown();
	}
	
	public MapLocation getRangerTargetLocation() {
		return Planet.getMapLocation(bcUnit.rangerTargetLocation());
	}
	
	public int getResearchLvel() {
		return (int)bcUnit.researchLevel();
	}
	
	public int getRocketBlastDamage() {
		return bcUnit.rocketBlastDamage();
	}
	
	public boolean isRocketUsed() {
		return bcUnit.rocketIsUsed()>0;
	}
	
	public int getRocketTravelTimeDecrease() {
		return (int)bcUnit.rocketTravelTimeDecrease();
	}
	
	public boolean isStructureBuilt() {
		return bcUnit.structureIsBuilt()>0;
	}
	
	public int getStructureMaxCapacity() {
		return (int)bcUnit.structureMaxCapacity();
	}
	
	public int getVisionRange() {
		return (int)bcUnit.visionRange();
	}
	
	public int getWorkerBuildHealth() {
		return (int)bcUnit.workerBuildHealth();
	}
	
	public int getWorkerHarvestAmount() {
		return (int)bcUnit.workerHarvestAmount();
	}
	
	public boolean hasWorkerActed() {
		return bcUnit.workerHasActed()>0;
	}
	
	public int getWorkerRepairHealth() {
		return (int)bcUnit.workerRepairHealth();
	}
	
	public void attack(Unit target) {
		gcInstance.getBcGameController().attack(id, target.getId());
	}
	
	public void beginSnipe(MapLocation location) {
		gcInstance.getBcGameController().beginSnipe(id, location.getBcMapLocation());
	}
	
	public void blink(MapLocation location) {
		gcInstance.getBcGameController().blink(id, location.getBcMapLocation());
		this.location.setMapLocation(location);
	}
	
	public void blueprint(UnitType structureType, Direction direction) {
		gcInstance.getBcGameController().blueprint(id, structureType.getBcUnitType(), direction.getBcDirection());
	}
	
	public void build(Unit target) {
		gcInstance.getBcGameController().build(id, target.getId());
	}
	
	public boolean canAttack(Unit target) {
		return gcInstance.getBcGameController().canAttack(id, target.getId());
	}
	
	public boolean canBeginSnipe(MapLocation location) {
		return gcInstance.getBcGameController().canBeginSnipe(id, location.getBcMapLocation());
	}
	
	public boolean canBlink(MapLocation location) {
		return gcInstance.getBcGameController().canBlink(id, location.getBcMapLocation());
	}

	public Unit[] senseNearbyUnitsByTeam(long radius, Team team) {
		return LibraryUtil.toArray(gcInstance.getBcGameController().senseNearbyUnitsByTeam(bcUnit.location().mapLocation(), radius, team.getBcTeam()));
	}

	public Unit[] senseNearbyUnitsByType(long radius, UnitType type) {
		return LibraryUtil.toArray(gcInstance.getBcGameController().senseNearbyUnitsByType(bcUnit.location().mapLocation(), radius, type.getBcUnitType()));
	}

	public Unit[] senseNearbyUnits(long radius) {
		return LibraryUtil.toArray(gcInstance.getBcGameController().senseNearbyUnits(bcUnit.location().mapLocation(), radius));
	}

	public Unit[] senseNearbyUnitsByFilter(long radius, Predicate<? super Unit> predicate) {
		return LibraryUtil.toFilteredArray(gcInstance.getBcGameController().senseNearbyUnits(bcUnit.location().mapLocation(), radius), predicate);
	}
	
	public boolean canBlueprint(UnitType unitType, Direction direction) {
		return gcInstance.getBcGameController().canBlueprint(id, unitType.getBcUnitType(), direction.getBcDirection());
	}
	
	public boolean canBuild(Unit target) {
		return gcInstance.getBcGameController().canBuild(id, target.getId());
	}
	
	public boolean canHarvest(Direction direction) {
		return gcInstance.getBcGameController().canHarvest(id, direction.getBcDirection());
	}
	
	public boolean canHeal(Unit target) {
		return gcInstance.getBcGameController().canHeal(id, target.getId());
	}
	
	public boolean canJavelin(Unit target) {
		return gcInstance.getBcGameController().canJavelin(id, target.getId());
	}
	
	public boolean canLaunchRocket(MapLocation location) {
		return gcInstance.getBcGameController().canLaunchRocket(id, location.getBcMapLocation());
	}
	
	public boolean canLoad(Unit target) {
		return gcInstance.getBcGameController().canLoad(id, target.getId());
	}
	
	public boolean canMove(Direction direction) {
		return gcInstance.getBcGameController().canMove(id, direction.getBcDirection());
	}
	
	public boolean canOvercharge(Unit target) {
		return gcInstance.getBcGameController().canOvercharge(id, target.getId());
	}
	
	public boolean canProduceRobot(UnitType type) {
		return gcInstance.getBcGameController().canProduceRobot(id, type.getBcUnitType());
	}
	
	public boolean canRepair(Unit target) {
		return gcInstance.getBcGameController().canRepair(id, target.id);
	}
	
	public boolean canReplicate(Direction direction) {
		return gcInstance.getBcGameController().canReplicate(id, direction.getBcDirection());
	}
	
	public boolean canUnload(Direction direction) {
		return gcInstance.getBcGameController().canUnload(id, direction.getBcDirection());
	}

	public void unload(Direction direction) {
		gcInstance.getBcGameController().unload(id, direction.getBcDirection());
	}
	
	public void disintegrate() {
		gcInstance.getBcGameController().disintegrateUnit(id);
	}
	
	public void harvest(Direction direction) {
		gcInstance.getBcGameController().harvest(id, direction.getBcDirection());
	}
	
	public void heal(Unit target) {
		gcInstance.getBcGameController().heal(id, target.getId());
	}

	public int getHealth() {
		return (int)bcUnit.health();
	}

	public int getMaxHealth() {
		return (int)bcUnit.maxHealth();
	}
	
	public boolean isAttackReady() {
		return gcInstance.getBcGameController().isAttackReady(id);
	}
	
	public boolean isBeginSnipeReady() {
		return gcInstance.getBcGameController().isBeginSnipeReady(id);
	}
	
	public boolean isBlinkReady() {
		return gcInstance.getBcGameController().isBlinkReady(id);
	}
	
	public boolean isHealReady() {
		return gcInstance.getBcGameController().isHealReady(id);
	}
	
	public boolean isJavelinReady() {
		return gcInstance.getBcGameController().isJavelinReady(id);
	}
	
	public boolean isMoveReady() {
		return gcInstance.getBcGameController().isMoveReady(id);
	}
	
	public boolean isOverchargeReady() {
		return gcInstance.getBcGameController().isOverchargeReady(id);
	}

	public int[] getGarrisonUnitIds() {
		return LibraryUtil.toArray(bcUnit.structureGarrison());
	}
	
	public void javelin(Unit target) {
		gcInstance.getBcGameController().javelin(id, target.getId());
	}
	
	public void launchRocket(MapLocation location) {
		gcInstance.getBcGameController().launchRocket(id, location.getBcMapLocation());
	}
	
	/**
	 * Loads a robot into this structure
	 * @param target robot to Load
	 */
	public void load(Unit target) {
		gcInstance.getBcGameController().load(id, target.getId());
	}
	
	public void move(Direction direction) {
		gcInstance.getBcGameController().moveRobot(id, direction.getBcDirection());
		location.setMapLocation(location.getMapLocation().getOffsetLocation(direction));
	}
	
	public void overcharge(Unit target) {
		gcInstance.getBcGameController().overcharge(id, target.getId());
	}
	
	public void produceRobot(UnitType type) {
		gcInstance.getBcGameController().produceRobot(id, type.getBcUnitType());
	}
	
	public void repair(Unit target) {
		gcInstance.getBcGameController().repair(id, target.getId());
	}
	
	public void replicate(Direction direction) {
		gcInstance.getBcGameController().replicate(id, direction.getBcDirection());
	}
	
	public int getId() {
		return id;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public UnitType getType() {
		return type;
	}
	
	public Team getTeam() {
		return team;
	}
}
