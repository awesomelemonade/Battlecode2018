package citricsky.battlecode2018.library.robot;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.structure.Structure;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;

public class Worker extends Robot {
	private int turnStep = 0;

	public Worker(bc.Unit bcUnit) {
		super(bcUnit);
	}

	@Override
	public void act(GameController gc) {
		if (!bcUnit.location().isOnMap()) return; // Make sure worker isn't in garrison or flying through space

		Unit[] nearby = senseNearbyUnitsByTeam(2, getTeam());
		boolean goOn = true;

		for (Unit unit : nearby) {
			if (unit instanceof Structure && canBuild(unit)) {
				build(unit);
				goOn = false;
			}
		}

		if (canBlueprint(UnitType.FACTORY, Direction.WEST)) {
			blueprint(UnitType.FACTORY, Direction.WEST);
			goOn = false;
		}

		if (!goOn) return;

		for (Direction dir : Direction.values()) {
			if (canHarvest(dir)) {
				harvest(dir);
			}
		}

		if (isMoveReady()) {
			for (Direction dir : Direction.values()) {
				if (canMove(dir)) {
					move(dir);
					break;
				}
			}
		}
	}

	public void harvest(Direction direction) {
		super.harvest(direction);
	}

	public boolean canHarvest(Direction direction) {
		return super.canHarvest(direction);
	}

	public void blueprint(UnitType structureType, Direction direction) {
		super.blueprint(structureType, direction);
	}

	public boolean canBlueprint(UnitType unitType, Direction direction) {
		return super.canBlueprint(unitType, direction);
	}

	public void build(Unit target) {
		super.build(target);
	}

	public boolean canBuild(Unit target) {
		return super.canBuild(target);
	}

	public void repair(Unit target) {
		super.repair(target);
	}

	public boolean canRepair(Unit target) {
		return super.canRepair(target);
	}

	public void replicate(Direction direction) {
		super.replicate(direction);
	}

	public boolean canReplicate(Direction direction) {
		return super.canReplicate(direction);
	}
}
