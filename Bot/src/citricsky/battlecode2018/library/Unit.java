package citricsky.battlecode2018.library;

public class Unit {
	protected bc.Unit bcUnit;
	private int id;
	private Team team;
	private UnitType type;
	private Location location;
	protected Unit(bc.Unit bcUnit) {
		this.bcUnit = bcUnit;
		//Initialize
		this.id = bcUnit.id();
		this.team = Team.valueOf(bcUnit.team());
		this.type = UnitType.valueOf(bcUnit.unitType());
	}
	public void attack(Unit target) {
		GameController.INSTANCE.getBcGameController().attack(id, target.getId());
	}
	public void beginSnipe(MapLocation location) {
		GameController.INSTANCE.getBcGameController().beginSnipe(id, location.getBcMapLocation());
	}
	public void blink(MapLocation location) {
		GameController.INSTANCE.getBcGameController().blink(id, location.getBcMapLocation());
	}
	public void blueprint(UnitType structureType, Direction direction) {
		GameController.INSTANCE.getBcGameController().blueprint(id, structureType.getBcUnitType(), direction.getBcDirection());
	}
	public void build(Unit target) {
		GameController.INSTANCE.getBcGameController().build(id, target.getId());
	}
	public boolean canAttack(Unit target) {
		return GameController.INSTANCE.getBcGameController().canAttack(id, target.getId());
	}
	public boolean canBeginSnipe(MapLocation location) {
		return GameController.INSTANCE.getBcGameController().canBeginSnipe(id, location.getBcMapLocation());
	}
	public boolean canBlink(MapLocation location) {
		return GameController.INSTANCE.getBcGameController().canBlink(id, location.getBcMapLocation());
	}
	public boolean canBlueprint(UnitType unitType, Direction direction) {
		return GameController.INSTANCE.getBcGameController().canBlueprint(id, unitType.getBcUnitType(), direction.getBcDirection());
	}
	public boolean canBuild(Unit target) {
		return GameController.INSTANCE.getBcGameController().canBuild(id, target.getId());
	}
	public boolean canHarvest(Direction direction) {
		return GameController.INSTANCE.getBcGameController().canHarvest(id, direction.getBcDirection());
	}
	public boolean canHeal(Unit target) {
		return GameController.INSTANCE.getBcGameController().canHeal(id, target.getId());
	}
	public boolean canJavelin(Unit target) {
		return GameController.INSTANCE.getBcGameController().canJavelin(id, target.getId());
	}
	public boolean canLaunchRocket(MapLocation location) {
		return GameController.INSTANCE.getBcGameController().canLaunchRocket(id, location.getBcMapLocation());
	}
	public boolean canLoad(Unit target) {
		return GameController.INSTANCE.getBcGameController().canLoad(id, target.getId());
	}
	public boolean canMove(Direction direction) {
		return GameController.INSTANCE.getBcGameController().canMove(id, direction.getBcDirection());
	}
	public boolean canOvercharge(Unit target) {
		return GameController.INSTANCE.getBcGameController().canOvercharge(id, target.getId());
	}
	public boolean canProduceRobot(UnitType type) {
		return GameController.INSTANCE.getBcGameController().canProduceRobot(id, type.getBcUnitType());
	}
	public boolean canRepair(Unit target) {
		return GameController.INSTANCE.getBcGameController().canRepair(id, target.id);
	}
	public boolean canReplicate(Direction direction) {
		return GameController.INSTANCE.getBcGameController().canReplicate(id, direction.getBcDirection());
	}
	public boolean canUnload(Direction direction) {
		return GameController.INSTANCE.getBcGameController().canUnload(id, direction.getBcDirection());
	}
	public void disintegrate() {
		GameController.INSTANCE.getBcGameController().disintegrateUnit(id);
	}
	public void harvest(Direction direction) {
		GameController.INSTANCE.getBcGameController().harvest(id, direction.getBcDirection());
	}
	public void heal(Unit target) {
		GameController.INSTANCE.getBcGameController().heal(id, target.getId());
	}
	public boolean isAttackReady() {
		return GameController.INSTANCE.getBcGameController().isAttackReady(id);
	}
	public boolean isBeginSnipeReady() {
		return GameController.INSTANCE.getBcGameController().isBeginSnipeReady(id);
	}
	public boolean isBlinkReady() {
		return GameController.INSTANCE.getBcGameController().isBlinkReady(id);
	}
	public boolean isHealReady() {
		return GameController.INSTANCE.getBcGameController().isHealReady(id);
	}
	public boolean isJavelinReady() {
		return GameController.INSTANCE.getBcGameController().isJavelinReady(id);
	}
	public boolean isMoveReady() {
		return GameController.INSTANCE.getBcGameController().isMoveReady(id);
	}
	public boolean isOverchargeReady() {
		return GameController.INSTANCE.getBcGameController().isOverchargeReady(id);
	}
	public void javelin(Unit target) {
		GameController.INSTANCE.getBcGameController().javelin(id, target.getId());
	}
	public void launchRocket(MapLocation location) {
		GameController.INSTANCE.getBcGameController().launchRocket(id, location.getBcMapLocation());
	}
	/**
	 * Loads a robot into this structure
	 * @param target Robot to Load
	 */
	public void load(Unit target) {
		GameController.INSTANCE.getBcGameController().load(id, target.getId());
	}
	public void move(Direction direction) {
		GameController.INSTANCE.getBcGameController().moveRobot(id, direction.getBcDirection());
	}
	public void overcharge(Unit target) {
		GameController.INSTANCE.getBcGameController().overcharge(id, target.getId());
	}
	public void produceRobot(UnitType type) {
		GameController.INSTANCE.getBcGameController().produceRobot(id, type.getBcUnitType());
	}
	public void repair(Unit target) {
		GameController.INSTANCE.getBcGameController().repair(id, target.getId());
	}
	public void replicate(Direction direction) {
		GameController.INSTANCE.getBcGameController().replicate(id, direction.getBcDirection());
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
