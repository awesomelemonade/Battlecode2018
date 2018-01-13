package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.util.Util;

public class EarthPlayer {
	private static PlanMap planMap;
	public static void execute() {
		GameController gc = GameController.INSTANCE;
		planMap = new PlanMap(Planet.EARTH.getStartingMap());
		UnitType[] researchOrder = new UnitType[]
				{UnitType.KNIGHT, UnitType.KNIGHT, UnitType.KNIGHT, UnitType.ROCKET,
						UnitType.HEALER, UnitType.HEALER, UnitType.HEALER};
		for(UnitType research: researchOrder) {
			gc.queueResearch(research);
		}
		while(true) {
			System.out.println("R: " + gc.getRoundNumber() + "; K: " + gc.getCurrentKarbonite());
			
			for(Unit unit: gc.getMyUnits()) {
				if(unit.getType() == UnitType.WORKER) {
					if(!tryBlueprint(unit, UnitType.FACTORY)) {
						if(!tryBuild(unit)) {
							Direction direction = Direction.randomDirection();
							if(unit.isMoveReady() && unit.canMove(direction)) {
								unit.move(direction);
							}
						}
					}
					//TODO: Harvesting of Karbonite
				}
				if(unit.getType() == UnitType.FACTORY) {
					if(unit.canProduceRobot(UnitType.KNIGHT)) {
						unit.produceRobot(UnitType.KNIGHT);
					}
					for(Direction direction: Direction.values()) {
						if(unit.canUnload(direction)) {
							unit.unload(direction);
						}
					}
				}
				if(unit.getType()==UnitType.KNIGHT) {
					BFSDestination bfs = new BFSDestination(unit.getLocation().getMapLocation().getPosition());
					bfs.process(vector -> Planet.EARTH.getMapLocation(vector).hasUnitAtLocation() &&
							Planet.EARTH.getMapLocation(vector).getUnit().getTeam() != gc.getTeam());
					if(bfs.getQueue().isEmpty()) {
						Direction direction = Direction.randomDirection();
						if(unit.isMoveReady() && unit.canMove(direction)) {
							unit.move(direction);
						}
					}else {
						Vector enemyPosition = bfs.getQueue().peekLast();
						Direction direction = bfs.trace(enemyPosition, unit.getLocation().getMapLocation().getPosition()).getOpposite();
						if(unit.isMoveReady() && unit.canMove(direction)) {
							unit.move(direction);
						}
						Unit enemyUnit = Planet.EARTH.getMapLocation(enemyPosition).getUnit();
						if(unit.isAttackReady() && unit.canJavelin(enemyUnit)) {
							unit.javelin(enemyUnit);
						}
						if(unit.isAttackReady() && unit.canAttack(enemyUnit)) {
							unit.attack(enemyUnit);
						}
					}
				}
			}
			gc.yield();
		}
	}
	public static boolean tryRepair(Unit unit) {
		Unit bestTarget = null;
		int bestHealth = 0;
		for(Direction direction: Direction.values()) {
			MapLocation offsetLocation = unit.getLocation().getMapLocation().getOffsetLocation(direction);
			if(offsetLocation.hasUnitAtLocation()) {
				Unit buildTarget = offsetLocation.getUnit();
				if(buildTarget.getType()==UnitType.FACTORY||buildTarget.getType()==UnitType.ROCKET) {
					if(bestHealth<buildTarget.getHealth()&&unit.canRepair(buildTarget)) {
						bestTarget = buildTarget;
						bestHealth = buildTarget.getHealth();
					}
				}
			}
		}
		if(bestTarget!=null) {
			unit.repair(bestTarget);
		}
		return bestTarget!=null;
	}
	public static boolean tryBuild(Unit unit) {
		Unit bestTarget = null;
		int bestHealth = 0;
		for(Direction direction: Direction.values()) {
			MapLocation offsetLocation = unit.getLocation().getMapLocation().getOffsetLocation(direction);
			if(offsetLocation.hasUnitAtLocation()) {
				Unit buildTarget = offsetLocation.getUnit();
				if(buildTarget.getType()==UnitType.FACTORY||buildTarget.getType()==UnitType.ROCKET) {
					if(bestHealth<buildTarget.getHealth()&&unit.canBuild(buildTarget)) {
						bestTarget = buildTarget;
						bestHealth = buildTarget.getHealth();
					}
				}
			}
		}
		if(bestTarget!=null) {
			unit.build(bestTarget);
		}
		return bestTarget!=null;
	}
	public static boolean tryBlueprint(Unit unit, UnitType type) {
		for(Direction direction: Direction.values()) {
			if(unit.canBlueprint(type, direction)) {
				MapLocation offsetLocation = unit.getLocation().getMapLocation().getOffsetLocation(direction);
				int neighbors = Util.getNeighbors(offsetLocation,
						(mapLocation)->planMap.isPassable(mapLocation.getPosition()));
				if(Util.canBuild(neighbors)) {
					unit.blueprint(type, direction);
					planMap.setStructure(offsetLocation.getPosition(), type);
					return true;
				}
			}
		}
		return false;
	}
}
