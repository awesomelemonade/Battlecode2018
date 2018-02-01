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
	private MoveManager moveManager;
	
	public FactoryExecutor(MoveManager moveManager) {
		this.moveManager = moveManager;
	}
	
	public UnitType getProduceType(MapLocation location) {
		if (RoundInfo.getRoundNumber() > 400 && RoundInfo.getRocketSpaces() < RoundInfo.getCombatUnitsCount()) {
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
		}
		if (RoundInfo.getRoundNumber() > 600) {
			return UnitType.MAGE;
		}
		if (moveManager.nearEnemy(location.getPosition(), 4, true)) {
			return UnitType.KNIGHT;
		}
		if (RoundInfo.getUnitCount(UnitType.WORKER) * 2 - 4 < RoundInfo.getUnitCount(UnitType.FACTORY)) {
			if(RoundInfo.getUnitCount(UnitType.WORKER) < 10) {
				return UnitType.WORKER;
			}		
		}
		if (RoundInfo.getUnitCount(UnitType.HEALER) == 0 && RoundInfo.getCombatUnitsCount() > 0) {
			return UnitType.HEALER;
		}
		if (((double)RoundInfo.getUnitCount(UnitType.HEALER)) < ((double)RoundInfo.getCombatUnitsCount()) / 2.0) {
			return UnitType.HEALER;
		}
		return UnitType.RANGER;
	}
	
	@Override
	public void execute(Unit unit) {
		if (!unit.isStructureBuilt()) {
			return;
		}
		UnitType produceType = getProduceType(unit.getLocation().getMapLocation());
		if (produceType != null && (produceType == UnitType.WORKER || RoundInfo.getCombatUnitsCount() < 70) && unit.canProduceRobot(produceType)) {
			unit.produceRobot(produceType);
		}
		int[] garrison = unit.getGarrisonUnitIds();
		if (garrison.length > 0) {
			Direction[] shuffled = Direction.shuffle(Direction.COMPASS);
			int shuffledIndex = 0;
			for (int i = 0; i < garrison.length; ++i) {
				Direction bestUnloadDirection = null;
				Vector bestUnloadPosition = null;
				int closestEnemy = Integer.MAX_VALUE;
				for (; shuffledIndex < shuffled.length; ++shuffledIndex) {
					if (unit.canUnload(shuffled[shuffledIndex])) {
						Vector position = unit.getLocation().getMapLocation().getPosition().add(shuffled[shuffledIndex].getOffsetVector());
						int bfsIndex = moveManager.getBFSIndex(RoundInfo.getUnitType(garrison[i]), Planet.EARTH, position, 1.0);
						int bfsStep = moveManager.getBFSStep(bfsIndex, position) - 1;
						if (closestEnemy == Integer.MAX_VALUE || bfsStep < closestEnemy) {
							closestEnemy = bfsStep;
							bestUnloadDirection = shuffled[shuffledIndex];
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
