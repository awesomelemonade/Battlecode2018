package citricsky.battlecode2018.library;

public class MapLocation {
	private bc.MapLocation bcMapLocation;
	private Planet planet;
	private Vector position;

	public MapLocation(bc.MapLocation bcMapLocation) {
		this.bcMapLocation = bcMapLocation;
		this.planet = Planet.valueOf(bcMapLocation.getPlanet());
		this.position = new Vector(bcMapLocation.getX(), bcMapLocation.getY());
	}
	
	public MapLocation(Planet planet, Vector position) {
		this.planet = planet;
		this.position = position;
	}

	public MapLocation getOffsetLocation(Direction direction) {
		return new MapLocation(planet, position.add(direction.getOffsetVector()));
	}
	
	public Planet getPlanet() {
		return planet;
	}

	public Vector getPosition() {
		return position;
	}

	//sensing? TODO
	//VecMapLocation allLocationsWithin(MapLocation location, long radiusSquared)
	//VecUnit senseNearbyUnits(MapLocation location, long radius)
	//VecUnit senseNearbyUnitsByTeam(MapLocation location, long radius, Team team)
	//VecUnit senseNearbyUnitsByType(MapLocation location, long radius, UnitType type)
	
	public Unit getUnit() {
		return new Unit(GameController.INSTANCE.getBcGameController().senseUnitAtLocation(bcMapLocation));
	}
	
	public boolean isOccupiable() {
		return GameController.INSTANCE.getBcGameController().isOccupiable(bcMapLocation)>0;
	}

	public boolean hasUnitAtLocation() {
		return GameController.INSTANCE.getBcGameController().hasUnitAtLocation(bcMapLocation);
	}

	public int getKarboniteCount() {
		return (int) GameController.INSTANCE.getBcGameController().karboniteAt(bcMapLocation);
	}
	
	protected bc.MapLocation getBcMapLocation(){
		if (bcMapLocation==null) {
			bcMapLocation = new bc.MapLocation(planet.getBcPlanet(), position.getX(), position.getY());
		}

		return bcMapLocation;
	}
}
