package citricsky.battlecode2018.unitexecutor;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.main.MoveManager;
import citricsky.battlecode2018.main.PlanetCommunication;
import citricsky.battlecode2018.main.RoundInfo;

public class RocketExecutor implements UnitExecutor {
	private MoveManager moveManager;
	private PlanetCommunication	planetCommunication;
	private int communicationIndex;
	public RocketExecutor(MoveManager moveManager, PlanetCommunication planetCommunication) {
		this.moveManager = moveManager;
		this.planetCommunication = planetCommunication;
		this.communicationIndex = 0;
	}
	
	public boolean shouldLaunch(Unit unit) {
		if(unit.getHealth() <= 50) {
			return true;
		}
		if(GameController.INSTANCE.getRoundNumber() > 748) {
			return true;
		}
		if(unit.getGarrisonUnitIds().length == unit.getStructureMaxCapacity()) {
			if(GameController.INSTANCE.getRoundNumber() < 500) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void execute(Unit unit) {
		if (!unit.isStructureBuilt()) {
			return;
		}
		if (unit.getLocation().getMapLocation().getPlanet() == Planet.EARTH) {
			if (shouldLaunch(unit)) {
				Vector candidate = planetCommunication.getLanding(communicationIndex);
				if (candidate != null) {
					MapLocation destination = Planet.MARS.getMapLocation(candidate);
					if (unit.canLaunchRocket(destination)) {
						System.out.println("Launching Rocket: " + destination);
						unit.launchRocket(destination);
						communicationIndex++;
						return;
					}
				}
				int offsetX = (int)(Math.random() * Planet.MARS.getWidth());
				int offsetY = (int)(Math.random() * Planet.MARS.getHeight());
				for(int x = 0; x < Planet.MARS.getWidth(); x++) {
					for(int y = 0; y < Planet.MARS.getHeight(); y++) {
						MapLocation destination = Planet.MARS.getMapLocation(
								(x + offsetX) % Planet.MARS.getWidth(), (y + offsetY) % Planet.MARS.getHeight());
						if(destination.isPassableTerrain()) {
							if (unit.canLaunchRocket(destination)) {
								System.out.println("Launching Random Rocket: " + destination);
								unit.launchRocket(destination);
								return;
							}
						}
					}
				}
				System.out.println("Unable to launch rocket");
			} else {
				int[] garrisoned = unit.getGarrisonUnitIds();
				boolean hasWorker = false;
				for (int id: garrisoned) {
					if (RoundInfo.getUnitType(id) == UnitType.WORKER) {
						hasWorker = true;
						break;
					}
				}
				for (Unit target : unit.senseNearbyUnitsByTeam(2, GameController.INSTANCE.getTeam())) {
					if (target.getType().isStructure()) continue;
					if (target.getType() == UnitType.WORKER && hasWorker && GameController.INSTANCE.getRoundNumber() < 739) continue;
					if (unit.canLoad(target)) {
						if (target.getType() == UnitType.WORKER) {
							hasWorker = true;
						}
						unit.load(target);
					}
				}
			}
		} else {
			int[] garrison = unit.getGarrisonUnitIds();
			for (int i = 0; i < garrison.length; ++i) {
				Direction bestUnloadDirection = null;
				int closestEnemy = Integer.MAX_VALUE;
				for (Direction direction: Direction.COMPASS) {
					if (unit.canUnload(direction)) {
						Vector position = unit.getLocation().getMapLocation().getPosition().add(direction.getOffsetVector());
						int bfsIndex = moveManager.getBFSIndex(RoundInfo.getUnitType(garrison[i]), Planet.MARS, position, 1.0);
						int bfsStep = moveManager.getBFSStep(bfsIndex, position) - 1;
						if (closestEnemy == Integer.MAX_VALUE || bfsStep < closestEnemy) {
							closestEnemy = bfsStep;
							bestUnloadDirection = direction;
						}
					}
				}
				if (bestUnloadDirection == null) {
					break;
				} else {
					unit.unload(bestUnloadDirection);
				}
			}
		}
	}
}
