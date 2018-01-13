package citricsky.battlecode2018.library;

public class Location {
	private bc.Location bcLocation;
	private MapLocation mapLocation;
	
	public Location(bc.Location bcLocation) {
		this.bcLocation = bcLocation;
		if(bcLocation.isOnMap()) {
			this.mapLocation = new MapLocation(bcLocation.mapLocation());
		}else {
			this.mapLocation = null;
		}
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
