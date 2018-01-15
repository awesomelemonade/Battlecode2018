package citricsky.battlecode2018.library;

import java.util.Objects;
import java.util.function.Predicate;

public class MapLocation {
	private bc.MapLocation bcMapLocation;
	private Planet planet;
	private Vector position;

	protected MapLocation(bc.MapLocation bcMapLocation) {
		this.bcMapLocation = bcMapLocation;
		this.planet = Planet.valueOf(bcMapLocation.getPlanet());
		this.position = new Vector(bcMapLocation.getX(), bcMapLocation.getY());
	}

	public MapLocation(Planet planet, int x, int y) {
		this.bcMapLocation = new bc.MapLocation(planet.getBcPlanet(), x, y);
		this.planet = planet;
		this.position = new Vector(x, y);
	}

	public MapLocation getOffsetLocation(Direction direction) {
		return this.getOffsetLocation(direction.getOffsetVector());
	}
	
	public MapLocation getOffsetLocation(Vector offset) {
		return planet.getMapLocation(position.add(offset));
	}
	
	public boolean isPassableTerrain() {
		return planet.getStartingMap().isPassableTerrainAt(this);
	}
	
	public boolean isOnMap() {
		return planet.getStartingMap().isOnMap(this);
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

	public Unit[] senseNearbyUnitsByFilter(long radiusSquared, Predicate<? super Unit> predicate) {
		return LibraryUtil.toFilteredArray(GameController.INSTANCE.getBcGameController().senseNearbyUnits(bcMapLocation, radiusSquared), predicate);
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
		return bcMapLocation;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof MapLocation) {
			MapLocation mapLocation = (MapLocation)o;
			return (planet == mapLocation.getPlanet() && position.equals(mapLocation.getPosition()));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(planet, position);
	}
}
