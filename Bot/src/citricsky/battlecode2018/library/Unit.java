package citricsky.battlecode2018.library;

@SuppressWarnings("unused")
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
	}
	
	public void attack(Unit target) {
		gcInstance.getBcGameController().attack(id, target.getId());
	}
	
	public void beginSnipe(MapLocation location) {
		gcInstance.getBcGameController().beginSnipe(id, location.getBcMapLocation());
	}
	
	public void blink(MapLocation location) {
		gcInstance.getBcGameController().blink(id, location.getBcMapLocation());
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
	
	public void disintegrate() {
		gcInstance.getBcGameController().disintegrateUnit(id);
	}
	
	public void harvest(Direction direction) {
		gcInstance.getBcGameController().harvest(id, direction.getBcDirection());
	}
	
	public void heal(Unit target) {
		gcInstance.getBcGameController().heal(id, target.getId());
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
	
	public void javelin(Unit target) {
		gcInstance.getBcGameController().javelin(id, target.getId());
	}
	
	public void launchRocket(MapLocation location) {
		gcInstance.getBcGameController().launchRocket(id, location.getBcMapLocation());
	}
	
	/**
	 * Loads a robot into this structure
	 * @param target Robot to Load
	 */
	public void load(Unit target) {
		gcInstance.getBcGameController().load(id, target.getId());
	}
	
	public void move(Direction direction) {
		gcInstance.getBcGameController().moveRobot(id, direction.getBcDirection());
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
