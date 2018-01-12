package citricsky.battlecode2018.library;

public class Location {
	private bc.Location bcLocation;
	private MapLocation mapLocation;
	
	public Location(bc.Location bcLocation) {
		this.bcLocation = bcLocation;
		this.mapLocation = new MapLocation(bcLocation.mapLocation());
	}
	public boolean isInSpace() {
		return bcLocation.isInSpace();
	}
	public boolean isInGarrison() {
		return bcLocation.isInGarrison();
	}
	public boolean isOnMap() {
		return bcLocation.isOnMap();
	}
	public MapLocation getMapLocation() {
		return mapLocation;
	}
}
