package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.main.MoveManager;
import citricsky.battlecode2018.main.RoundInfo;

public class FactoryExecutor implements UnitExecutor {
	public static boolean hasBeenDamaged = false;
	private MoveManager moveManager;
	
	public FactoryExecutor(MoveManager moveManager) {
		this.moveManager = moveManager;
	}
	
	public UnitType getProduceType(MapLocation location) {
		if ((RoundInfo.getRoundNumber() > 700 && RoundInfo.getCombatUnitsCount() > 5) ||
				(RoundInfo.getRoundNumber() > 600 && RoundInfo.getCombatUnitsCount() > 15) ||
				(RoundInfo.getRoundNumber() > 500 && RoundInfo.getCombatUnitsCount() > 35)) {
			if (RoundInfo.getRoundNumber() < 710) {
				if (RoundInfo.getUnitCount(UnitType.WORKER) < 8) {
					return UnitType.WORKER;
				}
			}
			return null;
		}
		/*if(location.getUnit().senseNearbyUnitsByTeam(16, GameController.INSTANCE.getEnemyTeam()).length > 12) {
			return UnitType.MAGE;
		}*/
		if (moveManager.nearEnemy(location.getPosition(), 7, true)) {
			return UnitType.KNIGHT;
		}
		if (RoundInfo.getUnitCount(UnitType.WORKER) * 2 - 4 < RoundInfo.getUnitCount(UnitType.FACTORY)) {
			if(RoundInfo.getUnitCount(UnitType.WORKER) < 10) {
				return UnitType.WORKER;
			}		
		}
		if ((float)RoundInfo.getUnitCount(UnitType.HEALER) < (float)RoundInfo.getCombatUnitsCount() / 2) {
			return UnitType.HEALER;
		}
		return UnitType.RANGER;
	}
	
	@Override
	public void execute(Unit unit) {
		if (!unit.isStructureBuilt()) {
			return;
		}
		else if(unit.getHealth() < unit.getMaxHealth()) {
			hasBeenDamaged = true;
		}
		UnitType produceType = getProduceType(unit.getLocation().getMapLocation());
		if (produceType != null && (produceType == UnitType.WORKER || RoundInfo.getCombatUnitsCount() < 70) && unit.canProduceRobot(produceType)) {
			unit.produceRobot(produceType);
		}
		int[] garrison = unit.getGarrisonUnitIds();
		if(RoundInfo.getRoundNumber() > 100 || hasBeenDamaged || 
				garrison.length == unit.getStructureMaxCapacity() ||
					unit.senseNearbyUnitsByTeam(17, GameController.INSTANCE.getEnemyTeam()).length > 1) {
			for (int i = 0; i < garrison.length; ++i) {
				Direction bestUnloadDirection = null;
				Vector bestUnloadPosition = null;
				int closestEnemy = Integer.MAX_VALUE;
				for (Direction direction: Direction.COMPASS) {
					if (unit.canUnload(direction)) {
						Vector position = unit.getLocation().getMapLocation().getPosition().add(direction.getOffsetVector());
						int bfsIndex = moveManager.getBFSIndex(RoundInfo.getUnitType(garrison[i]), Planet.EARTH, position, 1.0);
						int bfsStep = moveManager.getBFSStep(bfsIndex, position) - 1;
						if (closestEnemy == Integer.MAX_VALUE || bfsStep < closestEnemy) {
							closestEnemy = bfsStep;
							bestUnloadDirection = direction;
							bestUnloadPosition = position;
						}
					}
				}
				if (bestUnloadDirection == null) {
					break;
				} else {
					unit.unload(bestUnloadDirection);
					MapLocation location = GameController.INSTANCE.getPlanet().getMapLocation(bestUnloadPosition);
					moveManager.queueUnit(location.getUnit()); // throw the new unit into the queue
				}
			}
		}
	}
}
