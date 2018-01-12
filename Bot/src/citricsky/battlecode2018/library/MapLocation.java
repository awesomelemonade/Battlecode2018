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
	
	public MapLocation[] getAllMapLocationsWithin(int radiusSquared) {
		return LibraryUtil.toArray(GameController.INSTANCE.getBcGameController().allLocationsWithin(bcMapLocation, radiusSquared));
	}
	
	public Unit[] senseNearbyUnits(int radiusSquared) {
		return LibraryUtil.toArray(GameController.INSTANCE.getBcGameController().senseNearbyUnits(bcMapLocation, radiusSquared));
	}
	
	public Unit[] senseNearbyUnitsByTeam(int radiusSquared, Team team) {
		return LibraryUtil.toArray(GameController.INSTANCE.getBcGameController().senseNearbyUnitsByTeam(bcMapLocation, radiusSquared, team.getBcTeam()));
	}
	
	public Unit[] senseNearbyUnitsByType(int radiusSquared, UnitType type) {
		return LibraryUtil.toArray(GameController.INSTANCE.getBcGameController().senseNearbyUnitsByType(bcMapLocation, radiusSquared, type.getBcUnitType()));
	}
	
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
