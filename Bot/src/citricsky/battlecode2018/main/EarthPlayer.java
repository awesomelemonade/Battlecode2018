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
		for (UnitType research : researchOrder) {
			gc.queueResearch(research);
		}
		while (true) {
			System.out.println("R: " + gc.getRoundNumber() + "; K: " + gc.getCurrentKarbonite());
			//Unit[] enemyRobots = gc.getAllUnitsByFilter((unit -> unit.getTeam() != gc.getTeam() && !unit.isStructure()));

			boolean enemyInRange = true;
			for (Unit unit : gc.getMyUnitsByFilter(unit -> !unit.isStructure() && unit.getLocation().isOnMap())) {
				if (unit.senseNearbyUnitsByTeam((long) unit.getVisionRange(), gc.getEnemyTeam()).length > 0) {
					enemyInRange = false;
					break;
				}
			}

			for (Unit unit : gc.getMyUnitsByFilter(unit -> unit.getLocation().isOnMap())) { // Using a predicate up here, so there is a smaller array in memory
				try {
				/*if (!unit.getLocation().isOnMap()) {
					continue;
				}*/
					if (!enemyInRange) {
						//TODO: do something
					}
					if (unit.getType() == UnitType.WORKER) {
						if (!tryBlueprint(unit, UnitType.FACTORY)) {
							if (!tryBuild(unit)) {
								Direction direction = Direction.randomDirection();
								if (unit.isMoveReady() && unit.canMove(direction)) {
									unit.move(direction);
								}
							}
						}
						//TODO: Harvesting of Karbonite
						continue;
					}
					if (unit.getType() == UnitType.FACTORY) {
						if (unit.canProduceRobot(UnitType.KNIGHT)) {
							unit.produceRobot(UnitType.KNIGHT);
						}
						int garrisonSize = unit.getGarrisonUnitIds().length;
						if (garrisonSize > 0) {
							for (Direction direction : Direction.values()) {
								if (unit.canUnload(direction)) {
									unit.unload(direction);
									if (--garrisonSize == 0) {
										break;
									}
								}
							}
						}
					}
					if (unit.getType() == UnitType.KNIGHT) {
						BFSDestination bfs = new BFSDestination(planMap.getWidth(), planMap.getHeight(),
								unit.getLocation().getMapLocation().getPosition());
						bfs.process(vector -> planMap.isPassable(vector),
								vector -> Planet.EARTH.getMapLocation(vector).hasUnitAtLocation() &&
										Planet.EARTH.getMapLocation(vector).getUnit().getTeam() != gc.getTeam());
						if (bfs.getQueue().isEmpty()) {
							Direction direction = Direction.randomDirection();
							if (unit.isMoveReady() && unit.canMove(direction)) {
								unit.move(direction);
							}
						} else {
							Vector enemyPosition = bfs.getQueue().peekLast();
							Direction direction = bfs.trace(enemyPosition, unit.getLocation().getMapLocation().getPosition()).getOpposite();
							if (unit.isMoveReady() && unit.canMove(direction)) {
								unit.move(direction);
							}
							Unit enemyUnit = Planet.EARTH.getMapLocation(enemyPosition).getUnit();
							try {
								unit.javelin(enemyUnit);
							} catch (Exception ignored) {}
							if (unit.isAttackReady() && unit.canAttack(enemyUnit)) {
								unit.attack(enemyUnit);
							}
						}
						//continue; // Unnecessary
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			gc.yield();
		}
	}

	public static boolean tryRepair(Unit unit) {
		Unit bestTarget = null;
		int bestHealth = 0;
		for (Direction direction : Direction.values()) {
			MapLocation offsetLocation = unit.getLocation().getMapLocation().getOffsetLocation(direction);
			if (offsetLocation.hasUnitAtLocation()) {
				Unit buildTarget = offsetLocation.getUnit();
				if (buildTarget.getType() == UnitType.FACTORY || buildTarget.getType() == UnitType.ROCKET) {
					if (bestHealth < buildTarget.getHealth() && unit.canRepair(buildTarget)) {
						bestTarget = buildTarget;
						bestHealth = buildTarget.getHealth();
					}
				}
			}
		}
		if (bestTarget != null) {
			unit.repair(bestTarget);
		}
		return bestTarget != null;
	}

	public static boolean tryBuild(Unit unit) {
		Unit bestTarget = null;
		int bestHealth = 0;
		for (Direction direction : Direction.values()) {
			MapLocation offsetLocation = unit.getLocation().getMapLocation().getOffsetLocation(direction);
			if (offsetLocation.hasUnitAtLocation()) {
				Unit buildTarget = offsetLocation.getUnit();
				if (buildTarget.getType() == UnitType.FACTORY || buildTarget.getType() == UnitType.ROCKET) {
					if (bestHealth < buildTarget.getHealth() && unit.canBuild(buildTarget)) {
						bestTarget = buildTarget;
						bestHealth = buildTarget.getHealth();
					}
				}
			}
		}
		if (bestTarget != null) {
			unit.build(bestTarget);
		}
		return bestTarget != null;
	}

	public static boolean tryBlueprint(Unit unit, UnitType type) {
		for (Direction direction : Direction.values()) {
			if (unit.canBlueprint(type, direction)) {
				MapLocation offsetLocation = unit.getLocation().getMapLocation().getOffsetLocation(direction);
				if (offsetLocation.isOnMap()) {
					int neighbors = Util.getNeighbors(offsetLocation,
							(mapLocation) -> planMap.isPassable(mapLocation.getPosition()));
					if (Util.canBuild(neighbors)) {
						unit.blueprint(type, direction);
						planMap.setStructure(offsetLocation.getPosition(), type);
						return true;
					}
				}
			}
		}
		return false;
	}
}
